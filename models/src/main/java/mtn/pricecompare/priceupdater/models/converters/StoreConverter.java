package mtn.pricecompare.priceupdater.models.converters;

import mtn.pricecompare.priceupdater.lib.Store;
import mtn.pricecompare.priceupdater.models.entities.PriceEntity;
import mtn.pricecompare.priceupdater.models.entities.StoreEntity;

import java.util.List;
import java.util.stream.Collectors;

public class StoreConverter {

    public static Store toDto(StoreEntity entity) {

        Store dto = new Store();
        dto.setStoreId(entity.getId());
        dto.setStoreName(entity.getName());
        dto.setUrl(entity.getUrl());
        dto.setPriceList(entity.getPriceEntityList().stream()
                .map(pe -> PriceConverter.toDto(pe, true, false))
                .collect(Collectors.toList()));

        return dto;
    }

    public static StoreEntity toEntity(Store dto, List<PriceEntity> priceEntityList) {

        StoreEntity entity = new StoreEntity();
        entity.setName(dto.getStoreName());
        entity.setUrl(dto.getUrl());
        entity.setPriceEntityList(priceEntityList);

        return entity;
    }

}
