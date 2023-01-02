package mtn.rso.pricecompare.priceupdater.services.beans;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.kumuluz.ee.rest.utils.QueryStringDefaults;
import mtn.rso.pricecompare.priceupdater.lib.Request;
import mtn.rso.pricecompare.priceupdater.models.converters.RequestConverter;
import mtn.rso.pricecompare.priceupdater.models.entities.RequestEntity;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Log
@ApplicationScoped
public class RequestBean {

    private final Logger log = LogManager.getLogger(RequestBean.class.getName());

    @Inject
    private EntityManager em;

    // generic GET query for all entities
    @Counted(name = "requests_get_all_counter", description = "Displays the total number of getRequest() invocations that have occurred.")
    public List<Request> getRequest() {

        TypedQuery<RequestEntity> query = em.createNamedQuery("RequestEntity.getAll", RequestEntity.class);
        List<RequestEntity> resultList = query.getResultList();
        return resultList.stream().map(RequestConverter::toDto).collect(Collectors.toList());
    }

    // GET request with parameters
    @Counted(name = "requests_get_counter", description = "Displays the total number of getRequestFilter(uriInfo) invocations that have occurred.")
    public List<Request> getRequestFilter(UriInfo uriInfo) {

        QueryStringDefaults qsd = new QueryStringDefaults().maxLimit(200).defaultLimit(40).defaultOffset(0);
        QueryParameters query = qsd.builder().queryEncoded(uriInfo.getRequestUri().getRawQuery()).build();

        return JPAUtils.queryEntities(em, RequestEntity.class, query).stream()
                .map(RequestConverter::toDto).collect(Collectors.toList());
    }

    // POST
    @Counted(name = "request_create_counter", description = "Displays the total number of createRequest() invocations that have occurred.")
    public Request createRequest() {

        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setStatus(RequestConverter.statusToInteger("ACCEPTED"));
        requestEntity.setLastUpdated(Instant.now());

        try {
            beginTx();
            em.persist(requestEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (requestEntity.getId() == null) {
            log.error ("createRequest(): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }

        return RequestConverter.toDto(requestEntity);
    }

    // GET by id
    @Counted(name = "request_get_counter", description = "Displays the total number of getRequest(id) invocations that have occurred.")
    public Request getRequest(Integer id) {

        RequestEntity requestEntity = em.find(RequestEntity.class, id);
        if (requestEntity == null) {
            log.debug("getRequest(id): could not find entity.");
            throw new NotFoundException();
        }

        return RequestConverter.toDto(requestEntity);
    }

    // PUT by id
    @Counted(name = "request_put_counter", description = "Displays the total number of putRequest(id, status) invocations that have occurred.")
    public Request putRequest(Integer id, Integer status) {

        RequestEntity requestEntity = em.find(RequestEntity.class, id);
        if (requestEntity == null) {
            log.debug("putRequest(id, status): could not find entity.");
            throw new NotFoundException();
        }

        requestEntity.setStatus(status);
        requestEntity.setLastUpdated(Instant.now());

        try {
            beginTx();
            requestEntity = em.merge(requestEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.error("putRequest(id, status): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }

        return RequestConverter.toDto(requestEntity);
    }

    // DELETE by id
    @Counted(name = "request_delete_counter", description = "Displays the total number of deleteRequest(id) invocations that have occurred.")
    public boolean deleteRequest(Integer id) {

        RequestEntity requestEntity = em.find(RequestEntity.class, id);
        if (requestEntity == null) {
            log.debug("deleteRequest(id): could not find entity.");
            throw new NotFoundException();
        }

        try {
            beginTx();
            em.remove(requestEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.error("deleteRequest(id): could not remove entity.");
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
