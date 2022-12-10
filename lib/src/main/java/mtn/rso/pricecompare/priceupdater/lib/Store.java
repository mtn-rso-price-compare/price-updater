package mtn.rso.pricecompare.priceupdater.lib;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

public class Store {

    @Schema(example = "1")
    private Integer storeId;
    @Schema(example = "ENGROTUŠ d.o.o.")
    private String storeName;
    @Schema(example = "https://www.tus.si")
    private String url;
    private List<Price> priceList;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Price> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<Price> priceList) {
        this.priceList = priceList;
    }

}
