package org.devshred.tcx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class Track {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Trackpoint")
    private final List<Trackpoint> trackpoints = new ArrayList<>();

    public void addTrackpoint(Trackpoint trackpoint) {
        trackpoints.add(trackpoint);
    }
}
