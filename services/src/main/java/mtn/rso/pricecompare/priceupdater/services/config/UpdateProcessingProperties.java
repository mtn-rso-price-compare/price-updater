package mtn.rso.pricecompare.priceupdater.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("update-processing-properties")
public class UpdateProcessingProperties {

    @ConfigValue(watch = true)
    private Integer requestRetentionPeriod;

    @ConfigValue(watch = true)
    private Integer priceRetentionPeriod;

    @ConfigValue(watch = true)
    private Integer dataSource;

    @ConfigValue(watch = true)
    private Boolean updatePricesTus;

    @ConfigValue(watch = true)
    private Boolean updatePricesMercator;

    @ConfigValue(watch = true)
    private Boolean updatePricesSpar;

    public Integer getRequestRetentionPeriod() {
        return requestRetentionPeriod;
    }

    public void setRequestRetentionPeriod(Integer requestRetentionPeriod) {
        this.requestRetentionPeriod = requestRetentionPeriod;
    }

    public Integer getPriceRetentionPeriod() {
        return priceRetentionPeriod;
    }

    public void setPriceRetentionPeriod(Integer priceRetentionPeriod) {
        this.priceRetentionPeriod = priceRetentionPeriod;
    }

    public Integer getDataSource() {
        return dataSource;
    }

    public void setDataSource(Integer dataSource) {
        this.dataSource = dataSource;
    }

    public Boolean getUpdatePricesTus() {
        return updatePricesTus;
    }

    public void setUpdatePricesTus(Boolean updatePricesTus) {
        this.updatePricesTus = updatePricesTus;
    }

    public Boolean getUpdatePricesMercator() {
        return updatePricesMercator;
    }

    public void setUpdatePricesMercator(Boolean updatePricesMercator) {
        this.updatePricesMercator = updatePricesMercator;
    }

    public Boolean getUpdatePricesSpar() {
        return updatePricesSpar;
    }

    public void setUpdatePricesSpar(Boolean updatePricesSpar) {
        this.updatePricesSpar = updatePricesSpar;
    }

}
