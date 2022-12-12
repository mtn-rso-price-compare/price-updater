package mtn.rso.pricecompare.priceupdater.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import mtn.rso.pricecompare.priceupdater.lib.Request;
import mtn.rso.pricecompare.priceupdater.models.converters.RequestConverter;
import mtn.rso.pricecompare.priceupdater.models.entities.RequestEntity;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RequestScoped
public class RequestBean {

    private Logger log = Logger.getLogger(RequestBean.class.getName());

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
    @Counted(name = "requests_get_counter", description = "Displays the total number of getRequest(uriInfo) invocations that have occurred.")
    public List<Request> getRequestFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0).build();

        return JPAUtils.queryEntities(em, RequestEntity.class, queryParameters).stream()
                .map(RequestConverter::toDto).collect(Collectors.toList());
    }

    // POST
    @Counted(name = "request_create_counter", description = "Displays the total number of createRequest() invocations that have occurred.")
    public Request createRequest() {

        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setStatus(RequestConverter.statusToInteger("PROCESSING"));
        requestEntity.setLastUpdated(Instant.now());

        try {
            beginTx();
            em.persist(requestEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (requestEntity.getId() == null)
            throw new RuntimeException("Entity was not persisted");
        return RequestConverter.toDto(requestEntity);
    }

    // GET by id
    @Counted(name = "request_get_counter", description = "Displays the total number of getRequest(id) invocations that have occurred.")
    public Request getRequest(Integer id) {

        RequestEntity requestEntity = em.find(RequestEntity.class, id);
        if (requestEntity == null)
            throw new NotFoundException();

        return RequestConverter.toDto(requestEntity);
    }

    // PUT by id
    @Counted(name = "request_put_counter", description = "Displays the total number of putRequest(id, status) invocations that have occurred.")
    public Request putRequest(Integer id, Integer status) {

        RequestEntity requestEntity = em.find(RequestEntity.class, id);
        if (requestEntity == null)
            throw new NotFoundException();

        try {
            beginTx();
            requestEntity.setStatus(status);
            requestEntity.setLastUpdated(Instant.now());
            requestEntity = em.merge(requestEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            throw new RuntimeException("Entity was not persisted");
        }

        return RequestConverter.toDto(requestEntity);
    }

    // DELETE by id
    @Counted(name = "request_delete_counter", description = "Displays the total number of deleteRequest(id) invocations that have occurred.")
    public boolean deleteRequest(Integer id) {

        RequestEntity requestEntity = em.find(RequestEntity.class, id);
        if (requestEntity == null)
            throw new NotFoundException();

        try {
            beginTx();
            em.remove(requestEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
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
