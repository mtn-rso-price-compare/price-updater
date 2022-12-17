package mtn.rso.pricecompare.priceupdater.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import mtn.rso.pricecompare.priceupdater.lib.Price;
import mtn.rso.pricecompare.priceupdater.models.converters.PriceConverter;
import mtn.rso.pricecompare.priceupdater.models.entities.ItemEntity;
import mtn.rso.pricecompare.priceupdater.models.entities.PriceEntity;
import mtn.rso.pricecompare.priceupdater.models.entities.PriceKey;
import mtn.rso.pricecompare.priceupdater.models.entities.StoreEntity;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RequestScoped
public class PriceBean {

    private Logger log = Logger.getLogger(PriceBean.class.getName());

    @Inject
    private EntityManager em;

    // generic GET query for all entities
    @Counted(name = "prices_get_all_counter", description = "Displays the total number of getPrice() invocations that have occurred.")
    public List<Price> getPrice() {
        log.log(Level.FINER, "getPrice() entry.");

        TypedQuery<PriceEntity> query = em.createNamedQuery("PriceEntity.getAll", PriceEntity.class);
        List<PriceEntity> resultList = query.getResultList();
        List<Price> returnValue = resultList.stream().map(pe -> PriceConverter.toDto(pe, true, true))
                .collect(Collectors.toList());

        log.log(Level.FINER, "getPrice() return.");
        return returnValue;
    }

    // GET request with parameters
    @Counted(name = "prices_get_counter", description = "Displays the total number of getPriceFilter(urInfo) invocations that have occurred.")
    public List<Price> getPriceFilter(UriInfo uriInfo) {
        log.log(Level.FINER, "getPriceFilter(uriInfo) entry.");

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0).build();
        List<Price> returnValue = JPAUtils.queryEntities(em, PriceEntity.class, queryParameters).stream()
                .map(pe -> PriceConverter.toDto(pe, true, true)).collect(Collectors.toList());

        log.log(Level.FINER, "getPriceFilter(uriInfo) return.");
        return returnValue;
    }

    // POST
    @Counted(name = "price_create_counter", description = "Displays the total number of createPrice(price) invocations that have occurred.")
    public Price createPrice(Price price) {
        log.log(Level.FINER, "createPrice(price) entry.");

        ItemEntity itemEntity = em.find(ItemEntity.class, price.getItemId());
        StoreEntity storeEntity = em.find(StoreEntity.class, price.getStoreId());
        if (itemEntity == null || storeEntity == null) {
            log.log(Level.FINE, "createPrice(price): did not create entity due to missing relations.");
            throw new NotFoundException();
        }
        PriceEntity priceEntity = PriceConverter.toEntity(price, itemEntity, storeEntity);

        try {
            beginTx();
            em.persist(priceEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (priceEntity.getId() == null) {
            log.log(Level.WARNING, "createPrice(price): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }

        Price returnValue = PriceConverter.toDto(priceEntity, true, true);
        log.log(Level.FINER, "createPrice(price) return.");
        return returnValue;
    }

    // GET by id
    @Counted(name = "price_get_counter", description = "Displays the total number of getPrice(priceKey) invocations that have occurred.")
    public Price getPrice(PriceKey priceKey) {
        log.log(Level.FINER, "getPrice(priceKey) entry.");

        PriceEntity priceEntity = em.find(PriceEntity.class, priceKey);
        if (priceEntity == null) {
            log.log(Level.FINE, "getPrice(priceKey): could not find entity.");
            throw new NotFoundException();
        }

        Price returnValue = PriceConverter.toDto(priceEntity, true, true);
        log.log(Level.FINER, "getPrice(priceKey) return.");
        return returnValue;
    }

    // PUT by id
    @Counted(name = "price_put_counter", description = "Displays the total number of putPrice(price) invocations that have occurred.")
    public Price putPrice(Price price) {
        log.log(Level.FINER, "putPrice(price) entry.");

        PriceKey priceKey = new PriceKey();
        priceKey.setItemId(price.getItemId());
        priceKey.setStoreId(price.getStoreId());

        PriceEntity priceEntity = em.find(PriceEntity.class, priceKey);
        ItemEntity itemEntity = em.find(ItemEntity.class, price.getItemId());
        StoreEntity storeEntity = em.find(StoreEntity.class, price.getStoreId());

        if (priceEntity == null || itemEntity == null || storeEntity == null) {
            log.log(Level.FINE, "putPrice(price): could not find entity or its relations.");
            throw new NotFoundException();
        }
        PriceEntity updatedPriceEntity = PriceConverter.toEntity(price, itemEntity, storeEntity);
        PriceConverter.completeEntity(updatedPriceEntity, priceEntity);

        try {
            beginTx();
            updatedPriceEntity.setId(priceEntity.getId());
            updatedPriceEntity = em.merge(updatedPriceEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.log(Level.WARNING, "putPrice(price): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }

        Price returnValue = PriceConverter.toDto(updatedPriceEntity, true, true);
        log.log(Level.FINER, "putPrice(price) return.");
        return returnValue;
    }

    // DELETE by id
    @Counted(name = "price_delete_counter", description = "Displays the total number of deletePrice(priceKey) invocations that have occurred.")
    public boolean deletePrice(PriceKey priceKey) {
        log.log(Level.FINER, "deletePrice(priceKey) entry.");

        PriceEntity priceEntity = em.find(PriceEntity.class, priceKey);
        if (priceEntity == null) {
            log.log(Level.FINE, "deletePrice(priceKey): could not find entity.");
            throw new NotFoundException();
        }

        try {
            beginTx();
            em.remove(priceEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.log(Level.WARNING, "deletePrice(priceKey): could not remove entity.");
            return false;
        }

        log.log(Level.FINER, "deletePrice(priceKey) return.");
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
