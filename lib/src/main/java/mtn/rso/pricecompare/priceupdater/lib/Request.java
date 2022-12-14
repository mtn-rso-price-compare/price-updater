package mtn.rso.pricecompare.priceupdater.lib;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

public class Request {

    @Schema(example = "1")
    private Integer requestId;
    @Schema(example = "PROCESSED")
    private String status;
    @Schema(type = SchemaType.STRING, format = "date-time", example = "'2022-11-26T10:45:48Z'")
    private Instant lastUpdated;

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
