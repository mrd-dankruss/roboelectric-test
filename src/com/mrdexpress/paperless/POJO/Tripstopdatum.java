
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
    "id",
    "flowid",
    "flow",
    "floworder",
    "desc",
    "payload",
    "payloadid",
    "complete",
    "flowdata"
})
public class Tripstopdatum {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("flowid")
    private Integer flowid;
    @JsonProperty("flow")
    private String flow;
    @JsonProperty("floworder")
    private Integer floworder;
    @JsonProperty("desc")
    private String desc;
    @JsonProperty("payload")
    private String payload;
    @JsonProperty("payloadid")
    private Integer payloadid;
    @JsonProperty("complete")
    private String complete;
    @JsonProperty("flowdata")
    private Flowdata flowdata;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("flowid")
    public Integer getFlowid() {
        return flowid;
    }

    @JsonProperty("flowid")
    public void setFlowid(Integer flowid) {
        this.flowid = flowid;
    }

    @JsonProperty("flow")
    public String getFlow() {
        return flow;
    }

    @JsonProperty("flow")
    public void setFlow(String flow) {
        this.flow = flow;
    }

    @JsonProperty("floworder")
    public Integer getFloworder() {
        return floworder;
    }

    @JsonProperty("floworder")
    public void setFloworder(Integer floworder) {
        this.floworder = floworder;
    }

    @JsonProperty("desc")
    public String getDesc() {
        return desc;
    }

    @JsonProperty("desc")
    public void setDesc(String desc) {
        this.desc = desc;
    }

    @JsonProperty("payload")
    public String getPayload() {
        return payload;
    }

    @JsonProperty("payload")
    public void setPayload(String payload) {
        this.payload = payload;
    }

    @JsonProperty("payloadid")
    public Integer getPayloadid() {
        return payloadid;
    }

    @JsonProperty("payloadid")
    public void setPayloadid(Integer payloadid) {
        this.payloadid = payloadid;
    }

    @JsonProperty("complete")
    public String getComplete() {
        return complete;
    }

    @JsonProperty("complete")
    public void setComplete(String complete) {
        this.complete = complete;
    }

    @JsonProperty("flowdata")
    public Flowdata getFlowdata() {
        return flowdata;
    }

    @JsonProperty("flowdata")
    public void setFlowdata(Flowdata flowdata) {
        this.flowdata = flowdata;
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
