
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
    "triporder",
    "destination",
    "address",
    "suburb",
    "notes",
    "status",
    "location",
    "contacts",
    "coords",
    "contactnumbers",
    "complete",
    "tripstopdata"
})
public class Tripstop {

    @JsonProperty("id")
    private String id;
    @JsonProperty("triporder")
    private Integer triporder;
    @JsonProperty("destination")
    private Destination destination;
    @JsonProperty("address")
    private String address;
    @JsonProperty("suburb")
    private String suburb;
    @JsonProperty("notes")
    private String notes;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("location")
    private String location;
    @JsonProperty("contacts")
    private Contacts contacts;
    @JsonProperty("coords")
    private Coords coords;
    @JsonProperty("contactnumbers")
    private List<Object> contactnumbers = new ArrayList<Object>();
    @JsonProperty("complete")
    private String complete;
    @JsonProperty("tripstopdata")
    private List<Tripstopdatum> tripstopdata = new ArrayList<Tripstopdatum>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("triporder")
    public Integer getTriporder() {
        return triporder;
    }

    @JsonProperty("triporder")
    public void setTriporder(Integer triporder) {
        this.triporder = triporder;
    }

    @JsonProperty("destination")
    public Destination getDestination() {
        return destination;
    }

    @JsonProperty("destination")
    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("suburb")
    public String getSuburb() {
        return suburb;
    }

    @JsonProperty("suburb")
    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    @JsonProperty("notes")
    public String getNotes() {
        return notes;
    }

    @JsonProperty("notes")
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("contacts")
    public Contacts getContacts() {
        return contacts;
    }

    @JsonProperty("contacts")
    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    @JsonProperty("coords")
    public Coords getCoords() {
        return coords;
    }

    @JsonProperty("coords")
    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    @JsonProperty("contactnumbers")
    public List<Object> getContactnumbers() {
        return contactnumbers;
    }

    @JsonProperty("contactnumbers")
    public void setContactnumbers(List<Object> contactnumbers) {
        this.contactnumbers = contactnumbers;
    }

    @JsonProperty("complete")
    public String getComplete() {
        return complete;
    }

    @JsonProperty("complete")
    public void setComplete(String complete) {
        this.complete = complete;
    }

    @JsonProperty("tripstopdata")
    public List<Tripstopdatum> getTripstopdata() {
        return tripstopdata;
    }

    @JsonProperty("tripstopdata")
    public void setTripstopdata(List<Tripstopdatum> tripstopdata) {
        this.tripstopdata = tripstopdata;
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


    /* Custom Methods */
    public List<String> getBagIds(){
        ArrayList<String> bags = new ArrayList<String>();
        for(int i = 0; i < this.tripstopdata.size(); i++){
            Tripstopdatum tsp = this.tripstopdata.get(i);
            if (tsp.getPayload().equals("bag")){
                bags.add(Integer.toString(tsp.getPayloadid()));
            }
        }
        return bags;
    }
}
