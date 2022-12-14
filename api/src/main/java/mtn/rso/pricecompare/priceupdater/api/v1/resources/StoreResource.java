package mtn.rso.pricecompare.priceupdater.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import mtn.rso.pricecompare.priceupdater.lib.Store;
import mtn.rso.pricecompare.priceupdater.services.beans.StoreBean;
import mtn.rso.pricecompare.priceupdater.services.config.ApiProperties;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


@Log
@ApplicationScoped
@Path("/store")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StoreResource {

    @Inject
    private StoreBean storeBean;

    @Inject
    private ApiProperties apiProperties;

    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get a list of all stores in the database.", summary = "Get all stores")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of stores",
                    headers = {@Header(
                            name = "X-Total-Count",
                            description = "Number of objects in list"
                    )},
                    content = @Content(schema = @Schema(implementation = Store.class, type = SchemaType.ARRAY))
            )
    })
    @GET
    public Response getStore(@Parameter(name = "returnPrice",
            description = "Determines if price information for stores should be returned.")
                                 @QueryParam("returnPrice") boolean returnPrice) {
        if(!uriInfo.getQueryParameters().containsKey("returnPrice"))
            returnPrice = apiProperties.getReturnAllStorePrices();

        List<Store> storeList = storeBean.getStoreFilter(uriInfo, returnPrice);
        return Response.status(Response.Status.OK).header("X-Total-Count", storeList.size())
                .entity(storeList).build();
    }


    @Operation(description = "Get a store and its prices by store ID.", summary = "Get store")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Store",
                    content = @Content(schema = @Schema(implementation = Store.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Store not found"
            )
    })
    @GET
    @Path("/{storeId}")
    public Response getStore(@Parameter(name = "store ID", required = true)
                                     @PathParam("storeId") Integer storeId) {

        Store store;
        try {
            store = storeBean.getStore(storeId);
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(store).build();
    }

}
