package mtn.rso.pricecompare.priceupdater.services.beans;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import mtn.rso.pricecompare.priceupdater.lib.Store;
import mtn.rso.pricecompare.priceupdater.models.converters.StoreConverter;
import mtn.rso.pricecompare.priceupdater.models.entities.StoreEntity;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Log
@RequestScoped
public class StoreBean {

    private final Logger log = LogManager.getLogger(StoreBean.class.getName());

    @Inject
    private EntityManager em;

    // generic GET query for all entities
    @Counted(name = "stores_get_all_counter", description = "Displays the total number of getStore() invocations that have occurred.")
    public List<Store> getStore() {

        TypedQuery<StoreEntity> query = em.createNamedQuery("StoreEntity.getAll", StoreEntity.class);
        List<StoreEntity> resultList = query.getResultList();
        return resultList.stream().map(se -> StoreConverter.toDto(se, false))
                .collect(Collectors.toList());
    }

    // GET request with parameters
    @Counted(name = "stores_get_counter", description = "Displays the total number of getStoreFilter(uriInfo) invocations that have occurred.")
    public List<Store> getStoreFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0).build();
        return JPAUtils.queryEntities(em, StoreEntity.class, queryParameters).stream()
                .map(se -> StoreConverter.toDto(se, false)).collect(Collectors.toList());
    }

    // POST
    // NOTE: Does not create price entities if included. Use PriceBean to persist those.
    @Counted(name = "store_create_counter", description = "Displays the total number of createStore(store) invocations that have occurred.")
    public Store createStore(Store store) {

        StoreEntity storeEntity = StoreConverter.toEntity(store, Collections.emptyList());

        try {
            beginTx();
            em.persist(storeEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (storeEntity.getId() == null) {
            log.warn("createStore(store): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }
        return StoreConverter.toDto(storeEntity, false);
    }

    // GET by id
    @Counted(name = "store_get_counter", description = "Displays the total number of getStore(id) invocations that have occurred.")
    public Store getStore(Integer id) {

        StoreEntity storeEntity = em.find(StoreEntity.class, id);
        if (storeEntity == null) {
            log.debug("getStore(id): could not find entity.");
            throw new NotFoundException();
        }

        return StoreConverter.toDto(storeEntity, true);
    }

    // PUT by id
    // NOTE: Does not update price entities if included. Use PriceBean to persist those.
    @Counted(name = "store_put_counter", description = "Displays the total number of putStore(id, store) invocations that have occurred.")
    public Store putStore(Integer id, Store store) {

        StoreEntity storeEntity = em.find(StoreEntity.class, id);
        if (storeEntity == null) {
            log.debug("putStore(id, store): could not find entity.");
            throw new NotFoundException();
        }
        StoreEntity updatedStoreEntity = StoreConverter.toEntity(store, storeEntity.getPriceEntityList());
        StoreConverter.completeEntity(updatedStoreEntity, storeEntity);

        try {
            beginTx();
            updatedStoreEntity.setId(storeEntity.getId());
            updatedStoreEntity = em.merge(updatedStoreEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.warn("putStore(id, store): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }

        return StoreConverter.toDto(updatedStoreEntity, false);
    }

    // DELETE by id
    // NOTE: It will fail if store has associated prices. Use PriceBean to delete those first.
    @Counted(name = "store_delete_counter", description = "Displays the total number of deleteStore(id) invocations that have occurred.")
    public boolean deleteStore(Integer id) {

        StoreEntity storeEntity = em.find(StoreEntity.class, id);
        if (storeEntity == null) {
            log.debug("deleteStore(id): could not find entity.");
            throw new NotFoundException();
        } else if(!storeEntity.getPriceEntityList().isEmpty()) {
            log.debug("deleteStore(id): did not remove entity due to existing relations.");
            return false;
        }

        try {
            beginTx();
            em.remove(storeEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.warn("deleteStore(id): could not remove entity.");
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
