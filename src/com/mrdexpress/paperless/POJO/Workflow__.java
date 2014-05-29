
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
    "userid",
    "completed",
    "daymilkrunids",
    "tripdate",
    "tripstarttime",
    "tripstops",
    "warehouse"
})
public class Workflow__ {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("userid")
    private Integer userid;
    @JsonProperty("completed")
    private String completed;
    @JsonProperty("daymilkrunids")
    private List<String> daymilkrunids = new ArrayList<String>();
    @JsonProperty("tripdate")
    private String tripdate;
    @JsonProperty("tripstarttime")
    private String tripstarttime;
    @JsonProperty("tripstops")
    private List<Tripstop> tripstops = new ArrayList<Tripstop>();
    @JsonProperty("warehouse")
    private Warehouse warehouse;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("userid")
    public Integer getUserid() {
        return userid;
    }

    @JsonProperty("userid")
    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    @JsonProperty("completed")
    public String getCompleted() {
        return completed;
    }

    @JsonProperty("completed")
    public void setCompleted(String completed) {
        this.completed = completed;
    }

    @JsonProperty("daymilkrunids")
    public List<String> getDaymilkrunids() {
        return daymilkrunids;
    }

    @JsonProperty("daymilkrunids")
    public void setDaymilkrunids(List<String> daymilkrunids) {
        this.daymilkrunids = daymilkrunids;
    }

    @JsonProperty("tripdate")
    public String getTripdate() {
        return tripdate;
    }

    @JsonProperty("tripdate")
    public void setTripdate(String tripdate) {
        this.tripdate = tripdate;
    }

    @JsonProperty("tripstarttime")
    public String getTripstarttime() {
        return tripstarttime;
    }

    @JsonProperty("tripstarttime")
    public void setTripstarttime(String tripstarttime) {
        this.tripstarttime = tripstarttime;
    }

    @JsonProperty("tripstops")
    public List<Tripstop> getTripstops() {
        return tripstops;
    }

    @JsonProperty("tripstops")
    public void setTripstops(List<Tripstop> tripstops) {
        this.tripstops = tripstops;
    }

    @JsonProperty("warehouse")
    public Warehouse getWarehouse() {
        return warehouse;
    }

    @JsonProperty("warehouse")
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
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
