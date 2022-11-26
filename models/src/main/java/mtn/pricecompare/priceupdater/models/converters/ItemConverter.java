package mtn.pricecompare.priceupdater.models.converters;

import mtn.pricecompare.priceupdater.lib.Item;
import mtn.pricecompare.priceupdater.models.entities.ItemEntity;
import mtn.pricecompare.priceupdater.models.entities.PriceEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ItemConverter {

    public static Item toDto(ItemEntity entity) {

        Item dto = new Item();
        dto.setItemId(entity.getId());
        dto.setItemName(entity.getName());
        dto.setPriceList(entity.getPriceEntityList().stream()
                .map(pe -> PriceConverter.toDto(pe, false, true))
                .collect(Collectors.toList()));

        return dto;
    }

    public static ItemEntity toEntity(Item dto, List<PriceEntity> priceEntityList) {

        ItemEntity entity = new ItemEntity();
        entity.setName(dto.getItemName());
        entity.setPriceEntityList(priceEntityList);

        return entity;
    }

}
