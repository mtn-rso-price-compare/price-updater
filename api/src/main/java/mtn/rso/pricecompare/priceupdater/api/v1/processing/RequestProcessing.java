package mtn.rso.pricecompare.priceupdater.api.v1.processing;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import mtn.rso.pricecompare.priceupdater.lib.Item;
import mtn.rso.pricecompare.priceupdater.lib.Price;
import mtn.rso.pricecompare.priceupdater.lib.Request;
import mtn.rso.pricecompare.priceupdater.lib.Store;
import mtn.rso.pricecompare.priceupdater.models.converters.RequestConverter;
import mtn.rso.pricecompare.priceupdater.models.entities.PriceKey;
import mtn.rso.pricecompare.priceupdater.services.beans.ItemBean;
import mtn.rso.pricecompare.priceupdater.services.beans.PriceBean;
import mtn.rso.pricecompare.priceupdater.services.beans.RequestBean;
import mtn.rso.pricecompare.priceupdater.services.beans.StoreBean;
import mtn.rso.pricecompare.priceupdater.services.config.GlobalProperties;
import mtn.rso.pricecompare.priceupdater.services.config.UpdateProcessingProperties;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.sql.Time;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;


@ApplicationScoped
public class RequestProcessing {

    private final Logger log = LogManager.getLogger(RequestProcessing.class.getName());

    @Inject
    private ItemBean itemBean;

    @Inject
    private StoreBean storeBean;

    @Inject
    private PriceBean priceBean;

    @Inject
    private RequestBean requestBean;

    @Inject
    private UpdateProcessingProperties updateProcessingProperties;

    @Inject
    private GlobalProperties globalProperties;

    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> requestDeletionSchedule;

    private CompletableFuture<Void> processingChain;

    Set<String> itemNames;
    Map<String, Double> pricesTus;
    Map<String, Double> pricesMercator;
    Map<String, Double> pricesSpar;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        processingChain = CompletableFuture.runAsync(this::initializeProcessing);
        executorService = Executors.newSingleThreadScheduledExecutor();

        String watchedKey = "update-processing-properties.request-deletion-check-interval";
        Optional<String> keyValue = ConfigurationUtil.getInstance().get(watchedKey);
        int delay;
        if(keyValue.isPresent()) {
            try {
                delay = Integer.parseInt(keyValue.get());
            } catch(NumberFormatException e) {
                delay = 3600;
            }
        } else
            delay = 3600;

        requestDeletionSchedule = executorService.scheduleAtFixedRate(
                this::deleteOldRequests, 60, delay, TimeUnit.SECONDS
        );

        ConfigurationUtil.getInstance().subscribe(watchedKey, (String key, String value) -> {

            if (watchedKey.equals(key)) {

                int newDelay;
                try {
                    newDelay = Integer.parseInt(value);
                } catch(NumberFormatException e) {
                    newDelay = 3600;
                }

                try {
                    requestDeletionSchedule.cancel(false);
                    requestDeletionSchedule = executorService.scheduleAtFixedRate(
                            this::deleteOldRequests, 60, newDelay, TimeUnit.SECONDS
                    );
                    log.info(String.format("Restarted request deletion schedule to run every %d seconds.", newDelay));

                } catch(Exception e) {
                    log.error("Could not stop existing request deletion schedule.", e);
                }

            }

        });
    }

    @Operation(description = "Process a request to update item prices.", summary = "Process request")
    @Timed(name = "request_processing_timer", description = "Displays the total number of nanoseconds it takes for processRequest(requestId) to execute.")
    public void processRequest(Integer requestId) {
        processingChain = processingChain.thenRunAsync(() -> {
            globalProperties.setAllRequestsProcessed(false);
            try {
                processUpdate(requestId);
            } catch(Exception e1) {
                log.error(String.format("processRequest(requestId): Request processing failed (requestId=%d).", requestId), e1);
                try {
                    requestBean.putRequest(requestId, RequestConverter.statusToInteger("FAILED"));
                } catch (Exception e2) {
                    log.error(String.format("processRequest(requestId): Could not change update request status to " +
                            "FAILED (requestId=%d). Continuing with update.", requestId), e2);
                }
            }
            globalProperties.setAllRequestsProcessed(true);
        });
    }

    private void initializeProcessing() {
        log.info("initializeProcessing(): Request processing initialized.");
    }

    private void processUpdate(Integer requestId) {
        log.info(String.format("processUpdate(requestId): Processing update request (requestId=%d).", requestId));

        try {
            requestBean.putRequest(requestId, RequestConverter.statusToInteger("PROCESSING"));
        } catch (Exception e) {
            log.error(String.format("processUpdate(requestId): Could not change update request status to " +
                    "PROCESSING (requestId=%d). Continuing with update.", requestId), e);
        }

        Instant currentTime = Instant.now();

        // Set correct data
        String tusName = "ENGROTUŠ d.o.o.";
        String mercatorName = "Mercator d.o.o.";
        String sparName = "SPAR SLOVENIJA d.o.o.";

        pricesTus = null;
        pricesMercator = null;
        pricesSpar = null;

        switch (updateProcessingProperties.getDataSource()) {
            case 1 -> {
                itemNames = new HashSet<>(UpdateDataSource01.itemNames);
                if(updateProcessingProperties.getUpdatePricesTus())
                    pricesTus = UpdateDataSource01.pricesTus;
                if(updateProcessingProperties.getUpdatePricesMercator())
                    pricesMercator = UpdateDataSource01.pricesMercator;
                if(updateProcessingProperties.getUpdatePricesSpar())
                    pricesSpar = UpdateDataSource01.pricesSpar;
            }
            case 2 -> {
                itemNames = new HashSet<>(UpdateDataSource02.itemNames);
                if(updateProcessingProperties.getUpdatePricesTus())
                    pricesTus = UpdateDataSource02.pricesTus;
                if(updateProcessingProperties.getUpdatePricesMercator())
                    pricesMercator = UpdateDataSource02.pricesMercator;
                if(updateProcessingProperties.getUpdatePricesSpar())
                    pricesSpar = UpdateDataSource02.pricesSpar;
            }
            default -> {
                log.warn(String.format("processUpdate(requestId): Unknown data source (%d) set. Aborting update.",
                        updateProcessingProperties.getDataSource()));
                return;
            }
        }

        // Ensure all stores are in database
        List<Store> storeList = storeBean.getStore();
        HashMap<String, Integer> storeIds = new HashMap<>();
        for(Store store : storeList)
            storeIds.put(store.getStoreName(), store.getStoreId());

        if(pricesTus == null)
            pricesTus = new HashMap<>();

        if(pricesMercator == null)
            pricesMercator = new HashMap<>();

        if(pricesSpar == null)
            pricesSpar = new HashMap<>();

        // Get existing items
        List<Item> existingItemList = itemBean.getItem();
        HashMap<String, Integer> itemIds = new HashMap<>();
        for(Item item : existingItemList)
            itemIds.put(item.getItemName(), item.getItemId());

        // Create item if it does not exist
        for(String itemName : itemNames) {
            if(!itemIds.containsKey(itemName)) {
                Item newItem = new Item();
                newItem.setItemName(itemName);
                try {
                    newItem = itemBean.createItem(newItem);
                    itemIds.put(itemName, newItem.getItemId());
                } catch(Exception e) {
                    log.error(String.format("processUpdate(requestId): Could not persist item (itemName=%s). " +
                            "Continuing update without this item.", itemName), e);
                }
            }
        }

        // Update prices
        for(String itemName : itemIds.keySet()) {
            updatePrice(itemIds.get(itemName), itemName, storeIds, tusName, pricesTus, currentTime);
            updatePrice(itemIds.get(itemName), itemName, storeIds, mercatorName, pricesMercator, currentTime);
            updatePrice(itemIds.get(itemName), itemName, storeIds, sparName, pricesSpar, currentTime);
        }

        try {
            requestBean.putRequest(requestId, RequestConverter.statusToInteger("PROCESSED"));
        } catch (Exception e) {
            log.error(String.format("processUpdate(requestId): Could not change update request status to " +
                    "PROCESSED (requestId=%d).", requestId), e);
        }
    }

    private void updatePrice(Integer itemId, String itemName, HashMap<String, Integer> storeIds,
                             String storeName, Map<String, Double> pricesStore, Instant currentTime) {

        if(pricesStore.containsKey(itemName)) {
            // Input new price if present
            Price newPrice = new Price();
            newPrice.setItemId(itemId);
            newPrice.setStoreId(storeIds.get(storeName));
            newPrice.setAmount(pricesStore.get(itemName));
            newPrice.setLastUpdated(currentTime);

            try {
                priceBean.createOrPutPrice(newPrice);
            } catch (Exception e) {
                log.error(String.format("updatePrice(itemId, itemName, storeIds, storeName, pricesStore, currentTime): " +
                        "Could not persist price (itemName=%s, storeName=%s). " +
                        "Continuing update without this price.", itemName, storeName), e);
            }

        } else if(storeIds.containsKey(storeName)) {
            // Otherwise, remove old price if expired
            try {
                PriceKey priceKey = new PriceKey(itemId, storeIds.get(storeName));
                Price existingPrice = priceBean.getPrice(priceKey);
                if(existingPrice.getLastUpdated().until(currentTime, ChronoUnit.SECONDS)
                        > updateProcessingProperties.getPriceRetentionPeriod())
                    priceBean.deletePrice(priceKey);
            } catch (NotFoundException ignored) { }
        }
    }

    private void deleteOldRequests() {
        log.trace("deleteOldRequests(): Checking for old database update requests.");
        Instant currentTime = Instant.now();
        List<Request> requestList = requestBean.getRequest();

        for(Request request : requestList) {
            if(request.getLastUpdated().until(currentTime, ChronoUnit.SECONDS)
                    > updateProcessingProperties.getRequestRetentionPeriod())
                processingChain.thenRunAsync(() -> deleteRequest(request.getRequestId()));
        }
    }

    private void deleteRequest(int requestId) {
        log.info(String.format("deleteRequest(requestId): Deleting update request (requestId=%d).", requestId));
        try {
            if(!requestBean.deleteRequest(requestId))
                log.error(String.format("deleteRequest(requestId): Could not delete request (requestId=%d).", requestId));
        } catch(NotFoundException e) {
            log.error(String.format("deleteRequest(requestId): Could not find request to delete (requestId=%d).", requestId), e);
        }
    }

}
