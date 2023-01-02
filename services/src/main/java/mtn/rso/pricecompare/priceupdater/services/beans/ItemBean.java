package mtn.rso.pricecompare.priceupdater.services.beans;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.kumuluz.ee.rest.utils.QueryStringDefaults;
import mtn.rso.pricecompare.priceupdater.lib.Item;
import mtn.rso.pricecompare.priceupdater.models.converters.ItemConverter;
import mtn.rso.pricecompare.priceupdater.models.entities.ItemEntity;
import mtn.rso.pricecompare.priceupdater.models.entities.PriceEntity;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Log
@ApplicationScoped
public class ItemBean {

    private final Logger log = LogManager.getLogger(ItemBean.class.getName());

    @Inject
    private EntityManager em;

    // generic GET query for all entities
    @Counted(name = "items_get_all_counter", description = "Displays the total number of getItem() invocations that have occurred.")
    public List<Item> getItem() {

        TypedQuery<ItemEntity> query = em.createNamedQuery("ItemEntity.getAll", ItemEntity.class);
        List<ItemEntity> resultList = query.getResultList();
        return resultList.stream().map(ie -> ItemConverter.toDto(ie, false))
                .collect(Collectors.toList());
    }

    // GET request with parameters
    @Counted(name = "items_get_counter", description = "Displays the total number of getItemFilter(uriInfo) invocations that have occurred.")
    public List<Item> getItemFilter(UriInfo uriInfo, Boolean returnPrice) {

        QueryStringDefaults qsd = new QueryStringDefaults().maxLimit(200).defaultLimit(40).defaultOffset(0);
        QueryParameters query = qsd.builder().queryEncoded(uriInfo.getRequestUri().getRawQuery()).build();
        List<ItemEntity> itemEntities = JPAUtils.queryEntities(em, ItemEntity.class, query);

        if(returnPrice) {
            for(ItemEntity itemEntity : itemEntities) {
                TypedQuery<PriceEntity> priceQuery = em.createNamedQuery("PriceEntity.getByItem", PriceEntity.class);
                priceQuery.setParameter("itemId", itemEntity.getId());
                itemEntity.setPriceEntityList(priceQuery.getResultList());
            }
        }

        return itemEntities.stream().map(ie -> ItemConverter.toDto(ie, returnPrice)).collect(Collectors.toList());
    }

    // POST
    // NOTE: Does not create price entities if included. Use PriceBean to persist those.
    @Counted(name = "item_create_counter", description = "Displays the total number of createItem(item) invocations that have occurred.")
    public Item createItem(Item item) {

        ItemEntity itemEntity = ItemConverter.toEntity(item, Collections.emptyList());

        try {
            beginTx();
            em.persist(itemEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (itemEntity.getId() == null) {
            log.error("createItem(item): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }

        return ItemConverter.toDto(itemEntity, false);
    }

    // GET by id
    @Counted(name = "item_get_counter", description = "Displays the total number of getItem(id) invocations that have occurred.")
    public Item getItem(Integer id) {

        ItemEntity itemEntity = em.find(ItemEntity.class, id);
        if (itemEntity == null) {
            log.debug("getItem(id): could not find entity.");
            throw new NotFoundException();
        }

        TypedQuery<PriceEntity> priceQuery = em.createNamedQuery("PriceEntity.getByItem", PriceEntity.class);
        priceQuery.setParameter("itemId", itemEntity.getId());
        itemEntity.setPriceEntityList(priceQuery.getResultList());

        return ItemConverter.toDto(itemEntity, true);
    }

    // PUT by id
    // NOTE: Does not update price entities if included. Use PriceBean to persist those.
    @Counted(name = "item_put_counter", description = "Displays the total number of putItem(id, item) invocations that have occurred.")
    public Item putItem(Integer id, Item item) {

        ItemEntity itemEntity = em.find(ItemEntity.class, id);
        if (itemEntity == null) {
            log.debug("putItem(id, item): could not find entity.");
            throw new NotFoundException();
        }
        ItemEntity updatedItemEntity = ItemConverter.toEntity(item, itemEntity.getPriceEntityList());
        ItemConverter.completeEntity(updatedItemEntity, itemEntity);

        try {
            beginTx();
            updatedItemEntity.setId(itemEntity.getId());
            updatedItemEntity = em.merge(updatedItemEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.error("putItem(id, item): could not persist entity.");
            throw new RuntimeException("Entity was not persisted");
        }

        return ItemConverter.toDto(updatedItemEntity, false);
    }

    // DELETE by id
    // NOTE: It will fail if item has associated prices. Use PriceBean to delete those first.
    @Counted(name = "item_delete_counter", description = "Displays the total number of deleteItem(id) invocations that have occurred.")
    public boolean deleteItem(Integer id) {

        ItemEntity itemEntity = em.find(ItemEntity.class, id);
        if (itemEntity == null) {
            log.debug("deleteItem(id): could not find entity.");
            throw new NotFoundException();
        } else if(!itemEntity.getPriceEntityList().isEmpty()) {
            log.debug("deleteItem(id): did not remove entity due to existing relations.");
            return false;
        }

        try {
            beginTx();
            em.remove(itemEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.error("deleteItem(id): could not remove entity.");
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
