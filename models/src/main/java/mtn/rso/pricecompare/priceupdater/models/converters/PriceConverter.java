package mtn.rso.pricecompare.priceupdater.models.converters;

import mtn.rso.pricecompare.priceupdater.lib.Price;
import mtn.rso.pricecompare.priceupdater.models.entities.ItemEntity;
import mtn.rso.pricecompare.priceupdater.models.entities.PriceEntity;
import mtn.rso.pricecompare.priceupdater.models.entities.PriceKey;
import mtn.rso.pricecompare.priceupdater.models.entities.StoreEntity;

public class PriceConverter {

    public static Price toDto(PriceEntity entity, boolean setItemId, boolean setStoreId) {

        Price dto = new Price();
        if (setItemId)
            dto.setItemId(entity.getId().getItemId());
        if(setStoreId)
            dto.setStoreId(entity.getId().getStoreId());
        dto.setAmount(entity.getAmount());
        dto.setLastUpdated(entity.getLastUpdated());

        return dto;
    }

    public static PriceEntity toEntity(Price dto, ItemEntity itemEntity, StoreEntity storeEntity) {

        PriceEntity entity = new PriceEntity();
        entity.setItemEntity(itemEntity);
        entity.setStoreEntity(storeEntity);
        entity.setId(new PriceKey(itemEntity.getId(), storeEntity.getId()));
        entity.setAmount(dto.getAmount());
        entity.setLastUpdated(dto.getLastUpdated());

        return entity;
    }

    public static void completeEntity(PriceEntity partialEntity, PriceEntity fullEntity) {
        if(partialEntity.getItemEntity() == null)
            partialEntity.setItemEntity(fullEntity.getItemEntity());
        if(partialEntity.getStoreEntity() == null)
            partialEntity.setStoreEntity(fullEntity.getStoreEntity());
        if(partialEntity.getId() == null)
            partialEntity.setId(fullEntity.getId());
        if(partialEntity.getAmount() == null)
            partialEntity.setAmount(fullEntity.getAmount());
        if(partialEntity.getLastUpdated() == null)
            partialEntity.setLastUpdated(fullEntity.getLastUpdated());
    }

}
