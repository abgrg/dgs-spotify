package com.example.spotifydemo.models;

import com.example.spotifydemo.generated.types.Track;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MappedTrack extends Track {

    @JsonSetter("track")
    public void setTrackProperties(JsonNode trackObject) {
        this.setId(trackObject.get("id").asText());
        this.setName(trackObject.get("name").asText());
        this.setDurationMs(trackObject.get("duration_ms").asInt());
        this.setExplicit(trackObject.get("explicit").asBoolean());
        this.setUri(trackObject.get("uri").asText());
    }

    public Track getTrack() {
        return this;
    }
}
