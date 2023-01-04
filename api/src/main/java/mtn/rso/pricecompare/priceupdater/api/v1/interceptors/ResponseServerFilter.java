package mtn.rso.pricecompare.priceupdater.api.v1.interceptors;

import org.apache.logging.log4j.ThreadContext;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class ResponseServerFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        if(ThreadContext.containsKey("uniqueRequestId"))
            ThreadContext.remove("uniqueRequestId");
    }
}
