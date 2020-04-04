package org.devshred.tcx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Lap {
    @JacksonXmlProperty(localName = "TotalTimeSeconds")
    Double totalTimeSeconds;
    @JacksonXmlProperty(localName = "DistanceMeters")
    Double distanceMeters;
    @JacksonXmlProperty(localName = "BeginPosition")
    Position beginPosition;
    @JacksonXmlProperty(localName = "EndPosition")
    Position endPosition;
    @JacksonXmlProperty(localName = "Intensity")
    String intensity;

    public Double getTotalTimeSeconds() {
        return totalTimeSeconds;
    }

    public void setTotalTimeSeconds(Double totalTimeSeconds) {
        this.totalTimeSeconds = totalTimeSeconds;
    }

    public Double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(Double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public void setBeginPosition(Position beginPosition) {
        this.beginPosition = beginPosition;
    }

    public void setEndPosition(Position endPosition) {
        this.endPosition = endPosition;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }
}
