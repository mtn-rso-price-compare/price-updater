package mtn.rso.pricecompare.priceupdater.services.beans;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
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
import java.util.stream.Collectors;


@Log
@RequestScoped
public class PriceBean {

    private final Logger log = LogManager.getLogger(PriceBean.class.getName());

    @Inject
    private EntityManager em;

    // generic GET query for all entities
    @Counted(name = "prices_get_all_counter", description = "Displays the total number of getPrice() invocations that have occurred.")
    public List<Price> getPrice() {

        TypedQuery<PriceEntity> query = em.createNamedQuery("PriceEntity.getAll", PriceEntity.class);
        List<PriceEntity> resultList = query.getResultList();
        return resultList.stream().map(pe -> PriceConverter.toDto(pe, true, true))
                .collect(Collectors.toList());
    }

    // GET request with parameters
    @Counted(name = "prices_get_counter", description = "Displays the total number of getPriceFilter(urInfo) invocations that have occurred.")
    public List<Price> getPriceFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0).build();
        return JPAUtils.queryEntities(em, PriceEntity.class, queryParameters).stream()
                .map(pe -> PriceConverter.toDto(pe, true, true)).collect(Collectors.toList());
    }

    // POST
    @Counted(name = "price_create_counter", description = "Displays the total number of createPrice(price) invocations that have occurred.")
    public Price createPrice(Price price) {

        ItemEntity itemEntity = em.find(ItemEntity.class, price.getItemId());
        StoreEntity storeEntity = em.find(StoreEntity.class, price.getStoreId());
        if (itemEntity == null || storeEntity == null) {
            log.debug("createPrice(price): did not create entity due to missing relations.");
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
            log.warn("createPrice(price): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }

        return PriceConverter.toDto(priceEntity, true, true);
    }

    // GET by id
    @Counted(name = "price_get_counter", description = "Displays the total number of getPrice(priceKey) invocations that have occurred.")
    public Price getPrice(PriceKey priceKey) {

        PriceEntity priceEntity = em.find(PriceEntity.class, priceKey);
        if (priceEntity == null) {
            log.debug("getPrice(priceKey): could not find entity.");
            throw new NotFoundException();
        }

        return PriceConverter.toDto(priceEntity, true, true);
    }

    // PUT by id
    @Counted(name = "price_put_counter", description = "Displays the total number of putPrice(price) invocations that have occurred.")
    public Price putPrice(Price price) {

        PriceKey priceKey = new PriceKey();
        priceKey.setItemId(price.getItemId());
        priceKey.setStoreId(price.getStoreId());

        PriceEntity priceEntity = em.find(PriceEntity.class, priceKey);
        ItemEntity itemEntity = em.find(ItemEntity.class, price.getItemId());
        StoreEntity storeEntity = em.find(StoreEntity.class, price.getStoreId());

        if (priceEntity == null || itemEntity == null || storeEntity == null) {
            log.debug("putPrice(price): could not find entity or its relations.");
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
            log.warn("putPrice(price): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }

        return PriceConverter.toDto(updatedPriceEntity, true, true);
    }

    // DELETE by id
    @Counted(name = "price_delete_counter", description = "Displays the total number of deletePrice(priceKey) invocations that have occurred.")
    public boolean deletePrice(PriceKey priceKey) {

        PriceEntity priceEntity = em.find(PriceEntity.class, priceKey);
        if (priceEntity == null) {
            log.debug("deletePrice(priceKey): could not find entity.");
            throw new NotFoundException();
        }

        try {
            beginTx();
            em.remove(priceEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.warn("deletePrice(priceKey): could not remove entity.");
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
