package mtn.rso.pricecompare.priceupdater.api.v1.health;

import mtn.rso.pricecompare.priceupdater.services.config.GlobalProperties;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Readiness
@ApplicationScoped
public class RequestProcessingHealthCheck implements HealthCheck {

    @Inject
    private GlobalProperties globalProperties;

    @Override
    public HealthCheckResponse call() {
        if (globalProperties.getAllRequestsProcessed())
            return HealthCheckResponse.up(RequestProcessingHealthCheck.class.getSimpleName());
        else
            return HealthCheckResponse.down(RequestProcessingHealthCheck.class.getSimpleName());
    }

}
