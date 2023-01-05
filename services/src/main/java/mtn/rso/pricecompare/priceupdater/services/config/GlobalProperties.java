package mtn.rso.pricecompare.priceupdater.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("global-properties")
public class GlobalProperties {

    private Boolean allRequestsProcessed;

    @ConfigValue(watch = true)
    private Boolean appLiveness;

    @ConfigValue(watch = true)
    private Boolean appReadiness;

    public Boolean getAllRequestsProcessed() {
        return allRequestsProcessed;
    }

    public void setAllRequestsProcessed(Boolean allRequestsProcessed) {
        this.allRequestsProcessed = allRequestsProcessed;
    }

    public Boolean getAppLiveness() {
        return appLiveness;
    }

    public void setAppLiveness(Boolean appLiveness) {
        this.appLiveness = appLiveness;
    }

    public Boolean getAppReadiness() {
        return appReadiness;
    }

    public void setAppReadiness(Boolean appReadiness) {
        this.appReadiness = appReadiness;
    }
}
