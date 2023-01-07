package mtn.rso.pricecompare.priceupdater.api.v1.interceptors;

import mtn.rso.pricecompare.priceupdater.services.config.GlobalProperties;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.util.UuidUtil;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Objects;

@Provider
@PreMatching
public class PreMatchingRequestFilter implements ContainerRequestFilter {

    @Inject
    GlobalProperties globalProperties;

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {

        if(!globalProperties.getAllRequestsProcessed() && !ctx.getUriInfo().getPath().matches("request.*"))
            ctx.abortWith(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());

        MultivaluedMap<String, String> headers = ctx.getHeaders();
        if(headers.containsKey("uniqueRequestId"))
            ThreadContext.put("uniqueRequestId", headers.getFirst("uniqueRequestId"));
        else
            ThreadContext.put("uniqueRequestId", UuidUtil.getTimeBasedUuid().toString());

    }
}
