
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
    "id",
    "bagid",
    "hubname",
    "hubcode",
    "xof",
    "mdx",
    "waybill_id",
    "parcel",
    "dimensions",
    "barcode",
    "status",
    "contents"
})
public class Parcel {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("bagid")
    private Integer bagid;
    @JsonProperty("hubname")
    private String hubname;
    @JsonProperty("hubcode")
    private String hubcode;
    @JsonProperty("xof")
    private String xof;
    @JsonProperty("mdx")
    private String mdx;
    @JsonProperty("waybill_id")
    private String waybill_id;
    @JsonProperty("parcel")
    private String parcel;
    @JsonProperty("dimensions")
    private Dimensions dimensions;
    @JsonProperty("barcode")
    private String barcode;
    @JsonProperty("status")
    private Status_ status;
    @JsonProperty("contents")
    private List<String> contents = new ArrayList<String>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("bagid")
    public Integer getBagid() {
        return bagid;
    }

    @JsonProperty("bagid")
    public void setBagid(Integer bagid) {
        this.bagid = bagid;
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

    @JsonProperty("xof")
    public String getXof() {
        return xof;
    }

    @JsonProperty("xof")
    public void setXof(String xof) {
        this.xof = xof;
    }

    @JsonProperty("mdx")
    public String getMdx() {
        return mdx;
    }

    @JsonProperty("mdx")
    public void setMdx(String mdx) {
        this.mdx = mdx;
    }

    @JsonProperty("waybill_id")
    public String getWaybill_id() {
        return waybill_id;
    }

    @JsonProperty("waybill_id")
    public void setWaybill_id(String waybill_id) {
        this.waybill_id = waybill_id;
    }

    @JsonProperty("parcel")
    public String getParcel() {
        return parcel;
    }

    @JsonProperty("parcel")
    public void setParcel(String parcel) {
        this.parcel = parcel;
    }

    @JsonProperty("dimensions")
    public Dimensions getDimensions() {
        return dimensions;
    }

    @JsonProperty("dimensions")
    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    @JsonProperty("barcode")
    public String getBarcode() {
        return barcode;
    }

    @JsonProperty("barcode")
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @JsonProperty("status")
    public Status_ getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status_ status) {
        this.status = status;
    }

    @JsonProperty("contents")
    public List<String> getContents() {
        return contents;
    }

    @JsonProperty("contents")
    public void setContents(List<String> contents) {
        this.contents = contents;
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
