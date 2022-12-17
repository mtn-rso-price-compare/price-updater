package mtn.rso.pricecompare.priceupdater.services.beans;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RequestScoped
public class StoreBean {

    private Logger log = Logger.getLogger(StoreBean.class.getName());

    @Inject
    private EntityManager em;

    // generic GET query for all entities
    @Counted(name = "stores_get_all_counter", description = "Displays the total number of getStore() invocations that have occurred.")
    public List<Store> getStore() {
        log.log(Level.FINER, "getStore() entry.");

        TypedQuery<StoreEntity> query = em.createNamedQuery("StoreEntity.getAll", StoreEntity.class);
        List<StoreEntity> resultList = query.getResultList();
        List<Store> returnValue = resultList.stream().map(se -> StoreConverter.toDto(se, false))
                .collect(Collectors.toList());

        log.log(Level.FINER, "getStore() return.");
        return returnValue;
    }

    // GET request with parameters
    @Counted(name = "stores_get_counter", description = "Displays the total number of getStoreFilter(uriInfo) invocations that have occurred.")
    public List<Store> getStoreFilter(UriInfo uriInfo) {
        log.log(Level.FINER, "getStoreFilter(uriInfo) entry.");

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0).build();
        List<Store> returnValue = JPAUtils.queryEntities(em, StoreEntity.class, queryParameters).stream()
                .map(se -> StoreConverter.toDto(se, false)).collect(Collectors.toList());

        log.log(Level.FINER, "getStoreFilter(uriInfo) return.");
        return returnValue;
    }

    // POST
    // NOTE: Does not create price entities if included. Use PriceBean to persist those.
    @Counted(name = "store_create_counter", description = "Displays the total number of createStore(store) invocations that have occurred.")
    public Store createStore(Store store) {
        log.log(Level.FINER, "createStore(store) entry.");
        StoreEntity storeEntity = StoreConverter.toEntity(store, Collections.emptyList());

        try {
            beginTx();
            em.persist(storeEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (storeEntity.getId() == null) {
            log.log(Level.WARNING, "createStore(store): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }
        Store returnValue = StoreConverter.toDto(storeEntity, false);

        log.log(Level.FINER, "createStore(store) return.");
        return returnValue;
    }

    // GET by id
    @Counted(name = "store_get_counter", description = "Displays the total number of getStore(id) invocations that have occurred.")
    public Store getStore(Integer id) {
        log.log(Level.FINER, "getStore(id) entry.");

        StoreEntity storeEntity = em.find(StoreEntity.class, id);
        if (storeEntity == null) {
            log.log(Level.FINE, "getStore(id): could not find entity.");
            throw new NotFoundException();
        }

        Store returnValue = StoreConverter.toDto(storeEntity, true);
        log.log(Level.FINER, "getStore(id) return.");
        return returnValue;
    }

    // PUT by id
    // NOTE: Does not update price entities if included. Use PriceBean to persist those.
    @Counted(name = "store_put_counter", description = "Displays the total number of putStore(id, store) invocations that have occurred.")
    public Store putStore(Integer id, Store store) {
        log.log(Level.FINER, "putStore(id, store) entry.");

        StoreEntity storeEntity = em.find(StoreEntity.class, id);
        if (storeEntity == null) {
            log.log(Level.FINE, "putStore(id, store): could not find entity.");
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
            log.log(Level.WARNING, "putStore(id, store): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }

        Store returnValue = StoreConverter.toDto(updatedStoreEntity, false);
        log.log(Level.FINER, "putStore(id, store) return.");
        return returnValue;
    }

    // DELETE by id
    // NOTE: It will fail if store has associated prices. Use PriceBean to delete those first.
    @Counted(name = "store_delete_counter", description = "Displays the total number of deleteStore(id) invocations that have occurred.")
    public boolean deleteStore(Integer id) {
        log.log(Level.FINER, "deleteStore(id) entry.");

        StoreEntity storeEntity = em.find(StoreEntity.class, id);
        if (storeEntity == null) {
            log.log(Level.FINE, "deleteStore(id): could not find entity.");
            throw new NotFoundException();
        } else if(!storeEntity.getPriceEntityList().isEmpty()) {
            log.log(Level.FINE, "deleteStore(id): did not remove entity due to existing relations.");
            return false;
        }

        try {
            beginTx();
            em.remove(storeEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.log(Level.WARNING, "deleteStore(id): could not remove entity.");
            return false;
        }

        log.log(Level.FINER, "deleteStore(id) return.");
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
