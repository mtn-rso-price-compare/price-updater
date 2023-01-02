package mtn.rso.pricecompare.priceupdater.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("api-properties")
public class ApiProperties {

    @ConfigValue(watch = true)
    private Boolean returnAllItemPrices;

    @ConfigValue(watch = true)
    private Boolean returnAllStorePrices;

    public Boolean getReturnAllItemPrices() {
        return returnAllItemPrices;
    }

    public void setReturnAllItemPrices(Boolean returnAllItemPrices) {
        this.returnAllItemPrices = returnAllItemPrices;
    }

    public Boolean getReturnAllStorePrices() {
        return returnAllStorePrices;
    }

    public void setReturnAllStorePrices(Boolean returnAllStorePrices) {
        this.returnAllStorePrices = returnAllStorePrices;
    }

}
