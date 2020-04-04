package org.devshred.tcx;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@JacksonXmlRootElement
public class CoursePoint {
    @JacksonXmlProperty(localName = "Name")
    private final String name;
    @JacksonXmlProperty(localName = "Time")
    private final ZonedDateTime time;
    @JacksonXmlProperty(localName = "Position")
    private final Position position;
    @JacksonXmlProperty(localName = "PointType")
    private final String pointType;

    public CoursePoint(String name, LocalDateTime time, Position position, String pointType) {
        this.name = name;
        this.time = time.atZone(ZoneOffset.UTC);
        this.position = position;
        this.pointType = pointType;
    }

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    public ZonedDateTime getTime() {
        return time;
    }
}
