
package com.mrdexpress.paperless.POJO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "item",
    "barcode",
    "hubname",
    "hubcode",
    "pos",
    "neg",
    "parcels"
})
public class Flowdata {

    @JsonProperty("item")
    private String item;
    @JsonProperty("barcode")
    private String barcode;
    @JsonProperty("hubname")
    private String hubname;
    @JsonProperty("hubcode")
    private String hubcode;
    @JsonProperty("pos")
    private String pos;
    @JsonProperty("neg")
    private String neg;
    @JsonProperty("parcels")
    private List<Parcel> parcels = new ArrayList<Parcel>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("item")
    public String getItem() {
        return item;
    }

    @JsonProperty("item")
    public void setItem(String item) {
        this.item = item;
    }

    @JsonProperty("barcode")
    public String getBarcode() {
        return barcode;
    }

    @JsonProperty("barcode")
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @JsonProperty("hubname")
    public String getHubname() {
        return hubname;
    }

    @JsonProperty("hubname")
    public void setHubname(String hubname) {
        this.hubname = hubname;
    }

    @JsonProperty("hubcode")
    public String getHubcode() {
        return hubcode;
    }

    @JsonProperty("hubcode")
    public void setHubcode(String hubcode) {
        this.hubcode = hubcode;
    }

    @JsonProperty("pos")
    public String getPos() {
        return pos;
    }

    @JsonProperty("pos")
    public void setPos(String pos) {
        this.pos = pos;
    }

    @JsonProperty("neg")
    public String getNeg() {
        return neg;
    }

    @JsonProperty("neg")
    public void setNeg(String neg) {
        this.neg = neg;
    }

    @JsonProperty("parcels")
    public List<Parcel> getParcels() {
        return parcels;
    }

    @JsonProperty("parcels")
    public void setParcels(List<Parcel> parcels) {
        this.parcels = parcels;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
