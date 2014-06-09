
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
    "hubname",
    "location",
    "coords"
})
public class Warehouse {

    @JsonProperty("hubname")
    private String hubname;
    @JsonProperty("location")
    private String location;
    @JsonProperty("coords")
    private Coords_ coords;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("hubname")
    public String getHubname() {
        return hubname;
    }

    @JsonProperty("hubname")
    public void setHubname(String hubname) {
        this.hubname = hubname;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("coords")
    public Coords_ getCoords() {
        return coords;
    }

    @JsonProperty("coords")
    public void setCoords(Coords_ coords) {
        this.coords = coords;
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
