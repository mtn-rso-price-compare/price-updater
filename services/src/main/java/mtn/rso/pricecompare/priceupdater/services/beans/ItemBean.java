package mtn.rso.pricecompare.priceupdater.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import mtn.rso.pricecompare.priceupdater.lib.Item;
import mtn.rso.pricecompare.priceupdater.models.converters.ItemConverter;
import mtn.rso.pricecompare.priceupdater.models.entities.ItemEntity;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RequestScoped
public class ItemBean {

    private Logger log = Logger.getLogger(ItemBean.class.getName());

    @Inject
    private EntityManager em;

    // generic GET query for all entities
    @Counted(name = "items_get_all_counter", description = "Displays the total number of getItem() invocations that have occurred.")
    public List<Item> getItem() {

        TypedQuery<ItemEntity> query = em.createNamedQuery("ItemEntity.getAll", ItemEntity.class);
        List<ItemEntity> resultList = query.getResultList();

        return resultList.stream().map(ie -> ItemConverter.toDto(ie, false)).collect(Collectors.toList());
    }

    // GET request with parameters
    @Counted(name = "items_get_counter", description = "Displays the total number of getItemFilter(uriInfo) invocations that have occurred.")
    public List<Item> getItemFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0).build();

        return JPAUtils.queryEntities(em, ItemEntity.class, queryParameters).stream()
                .map(ie -> ItemConverter.toDto(ie, false)).collect(Collectors.toList());
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

        if (itemEntity.getId() == null)
            throw new RuntimeException("Entity was not persisted");
        return ItemConverter.toDto(itemEntity, false);
    }

    // GET by id
    @Counted(name = "item_get_counter", description = "Displays the total number of getItem(id) invocations that have occurred.")
    public Item getItem(Integer id) {

        ItemEntity itemEntity = em.find(ItemEntity.class, id);
        if (itemEntity == null)
            throw new NotFoundException();

        return ItemConverter.toDto(itemEntity, true);
    }

    // PUT by id
    // NOTE: Does not update price entities if included. Use PriceBean to persist those.
    @Counted(name = "item_put_counter", description = "Displays the total number of putItem(id, item) invocations that have occurred.")
    public Item putItem(Integer id, Item item) {

        ItemEntity itemEntity = em.find(ItemEntity.class, id);
        if (itemEntity == null)
            throw new NotFoundException();
        ItemEntity updatedItemEntity = ItemConverter.toEntity(item, itemEntity.getPriceEntityList());
        ItemConverter.completeEntity(updatedItemEntity, itemEntity);

        try {
            beginTx();
            updatedItemEntity.setId(itemEntity.getId());
            updatedItemEntity = em.merge(updatedItemEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            throw new RuntimeException("Entity was not persisted");
        }

        return ItemConverter.toDto(updatedItemEntity, false);
    }

    // DELETE by id
    // NOTE: It will fail if item has associated prices. Use PriceBean to delete those first.
    @Counted(name = "item_delete_counter", description = "Displays the total number of deleteItem(id) invocations that have occurred.")
    public boolean deleteItem(Integer id) {

        ItemEntity itemEntity = em.find(ItemEntity.class, id);
        if (itemEntity == null)
            throw new NotFoundException();
        else if(!itemEntity.getPriceEntityList().isEmpty())
            return false;

        try {
            beginTx();
            em.remove(itemEntity);
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
