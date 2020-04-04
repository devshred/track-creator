package org.devshred.tcx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class Course {
    @JacksonXmlProperty(localName = "Name")
    private final String name;
    @JacksonXmlProperty(localName = "CoursePoint")
    @JacksonXmlElementWrapper(useWrapping = false)
    private final List<CoursePoint> coursePoints = new ArrayList<>();
    @JacksonXmlProperty(localName = "Lap")
    private Lap lap;
    @JacksonXmlProperty(localName = "Track")
    private Track track;

    public Course(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Lap getLap() {
        return lap;
    }

    public void setLap(Lap lap) {
        this.lap = lap;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public void addCoursePoint(CoursePoint coursePoint) {
        this.coursePoints.add(coursePoint);
    }
}
