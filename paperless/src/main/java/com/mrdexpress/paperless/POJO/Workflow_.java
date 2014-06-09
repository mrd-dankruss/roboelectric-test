
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
    "OK",
    "errors",
    "handover",
    "delays",
    "partial",
    "wifi",
    "workflow"
})
public class Workflow_ {

    @JsonProperty("OK")
    private Boolean oK;
    @JsonProperty("errors")
    private List<Object> errors = new ArrayList<Object>();
    @JsonProperty("handover")
    private List<Handover> handover = new ArrayList<Handover>();
    @JsonProperty("delays")
    private List<Delay> delays = new ArrayList<Delay>();
    @JsonProperty("partial")
    private List<Partial> partial = new ArrayList<Partial>();
    @JsonProperty("wifi")
    private List<Wifi> wifi = new ArrayList<Wifi>();
    @JsonProperty("workflow")
    private Workflow__ workflow;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("OK")
    public Boolean getOK() {
        return oK;
    }

    @JsonProperty("OK")
    public void setOK(Boolean oK) {
        this.oK = oK;
    }

    @JsonProperty("errors")
    public List<Object> getErrors() {
        return errors;
    }

    @JsonProperty("errors")
    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    @JsonProperty("handover")
    public List<Handover> getHandover() {
        return handover;
    }

    @JsonProperty("handover")
    public void setHandover(List<Handover> handover) {
        this.handover = handover;
    }

    @JsonProperty("delays")
    public List<Delay> getDelays() {
        return delays;
    }

    @JsonProperty("delays")
    public void setDelays(List<Delay> delays) {
        this.delays = delays;
    }

    @JsonProperty("partial")
    public List<Partial> getPartial() {
        return partial;
    }

    @JsonProperty("partial")
    public void setPartial(List<Partial> partial) {
        this.partial = partial;
    }

    @JsonProperty("wifi")
    public List<Wifi> getWifi() {
        return wifi;
    }

    @JsonProperty("wifi")
    public void setWifi(List<Wifi> wifi) {
        this.wifi = wifi;
    }

    @JsonProperty("workflow")
    public Workflow__ getWorkflow() {
        return workflow;
    }

    @JsonProperty("workflow")
    public void setWorkflow(Workflow__ workflow) {
        this.workflow = workflow;
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
