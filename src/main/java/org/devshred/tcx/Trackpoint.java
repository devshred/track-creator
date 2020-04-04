package org.devshred.tcx;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Trackpoint {
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JacksonXmlProperty(localName = "Time")
    private final ZonedDateTime time;
    @JacksonXmlProperty(localName = "Position")
    private final Position position;
    @JacksonXmlProperty(localName = "AltitudeMeters")
    private final Double altitudeMeters;
    @JacksonXmlProperty(localName = "DistanceMeters")
    private final Double distanceMeters;

    public Trackpoint(LocalDateTime time, Position position, Double altitudeMeters, Double distanceMeters) {
        this.time = time.atZone(ZoneOffset.UTC);
        this.position = position;
        this.altitudeMeters = altitudeMeters;
        this.distanceMeters = distanceMeters;
    }
}
