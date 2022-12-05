package mtn.rso.pricecompare.priceupdater.api.v1.processing;

import mtn.rso.pricecompare.priceupdater.lib.Item;
import mtn.rso.pricecompare.priceupdater.models.converters.RequestConverter;
import mtn.rso.pricecompare.priceupdater.services.beans.ItemBean;
import mtn.rso.pricecompare.priceupdater.services.beans.PriceBean;
import mtn.rso.pricecompare.priceupdater.services.beans.RequestBean;
import mtn.rso.pricecompare.priceupdater.services.beans.StoreBean;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

@RequestScoped
public class RequestProcessing {

    @Inject
    private ItemBean itemBean;

    @Inject
    private StoreBean storeBean;

    @Inject
    private PriceBean priceBean;

    @Inject
    private RequestBean requestBean;

    @Operation(description = "Process a request to update item prices.", summary = "Process request")
    public void processRequest(Integer requestId) {
        // TODO: Implement proper request processing

        Item item = new Item();
        item.setItemName("piščančja prsa");
        try {
            itemBean.putItem(1, item);
            try {
                requestBean.putRequest(requestId, RequestConverter.statusToInteger("SUCCESS"));
            } catch (Exception e) {
                // TODO: Report error
            }
        } catch (RuntimeException re) {
            try {
                requestBean.putRequest(requestId, RequestConverter.statusToInteger("FAILURE"));
            } catch (Exception e) {
                // TODO: Report error
            }
        }
    }
}
