package org.devshred.tcx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Position {
    @JacksonXmlProperty(localName = "LatitudeDegrees")
    private final Double latitudeDegrees;

    @JacksonXmlProperty(localName = "LongitudeDegrees")
    private final Double longitudeDegrees;

    public Position(Double latitudeDegrees, Double longitudeDegrees) {
        this.latitudeDegrees = latitudeDegrees;
        this.longitudeDegrees = longitudeDegrees;
    }
}
