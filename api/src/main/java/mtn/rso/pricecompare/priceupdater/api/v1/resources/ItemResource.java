package mtn.rso.pricecompare.priceupdater.api.v1.resources;

import mtn.rso.pricecompare.priceupdater.lib.Item;
import mtn.rso.pricecompare.priceupdater.services.beans.ItemBean;
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
import java.util.logging.Level;
import java.util.logging.Logger;


@ApplicationScoped
@Path("/item")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private Logger log = Logger.getLogger(ItemResource.class.getName());

    @Inject
    private ItemBean itemBean;

    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get a list of all items in the database.", summary = "Get all items")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of items",
                    headers = {@Header(
                            name = "X-Total-Count",
                            description = "Number of objects in list"
                    )},
                    content = @Content(schema = @Schema(implementation = Item.class, type = SchemaType.ARRAY))
            )
    })
    @GET
    public Response getItem() {
        log.log(Level.FINER, "getItem() entry.");

        List<Item> itemList = itemBean.getItemFilter(uriInfo);
        Response returnValue = Response.status(Response.Status.OK).header("X-Total-Count", itemList.size())
                .entity(itemList).build();

        log.log(Level.FINER, "getItem() return (200).");
        return returnValue;
    }


    @Operation(description = "Get an item and its prices by item ID.", summary = "Get item")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Item",
                    content = @Content(schema = @Schema(implementation = Item.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Item not found"
            )
    })
    @GET
    @Path("/{itemId}")
    public Response getItem(@Parameter(name = "item ID", required = true)
                                     @PathParam("itemId") Integer itemId) {
        log.log(Level.FINER, "getItem(itemId) entry.");

        Item item;
        try {
            item = itemBean.getItem(itemId);
        } catch (NotFoundException e) {
            Response returnValue = Response.status(Response.Status.NOT_FOUND).build();
            log.log(Level.FINER, "getItem(itemId) return (404).");
            return returnValue;
        }
        Response returnValue = Response.status(Response.Status.OK).entity(item).build();

        log.log(Level.FINER, "getItem(itemId) return (200).");
        return returnValue;
    }

}
