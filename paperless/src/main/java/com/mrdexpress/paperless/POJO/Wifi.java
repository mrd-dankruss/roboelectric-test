
package com.mrdexpress.paperless.POJO;

import java.util.HashMap;
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
    "SSID",
    "presharedkey",
    "priority",
    "notbroadcasting"
})
public class Wifi {

    @JsonProperty("SSID")
    private String sSID;
    @JsonProperty("presharedkey")
    private String presharedkey;
    @JsonProperty("priority")
    private Integer priority;
    @JsonProperty("notbroadcasting")
    private Boolean notbroadcasting;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("SSID")
    public String getSSID() {
        return sSID;
    }

    @JsonProperty("SSID")
    public void setSSID(String sSID) {
        this.sSID = sSID;
    }

    @JsonProperty("presharedkey")
    public String getPresharedkey() {
        return presharedkey;
    }

    @JsonProperty("presharedkey")
    public void setPresharedkey(String presharedkey) {
        this.presharedkey = presharedkey;
    }

    @JsonProperty("priority")
    public Integer getPriority() {
        return priority;
    }

    @JsonProperty("priority")
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @JsonProperty("notbroadcasting")
    public Boolean getNotbroadcasting() {
        return notbroadcasting;
    }

    @JsonProperty("notbroadcasting")
    public void setNotbroadcasting(Boolean notbroadcasting) {
        this.notbroadcasting = notbroadcasting;
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
