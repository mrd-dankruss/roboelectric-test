
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
    "type",
    "hubname",
    "hubcode"
})
public class Extra {

    @JsonProperty("type")
    private String type;
    @JsonProperty("hubname")
    private String hubname;
    @JsonProperty("hubcode")
    private String hubcode;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
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
