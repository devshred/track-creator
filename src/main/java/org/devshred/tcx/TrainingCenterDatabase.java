package org.devshred.tcx;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "TrainingCenterDatabase", namespace = "http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2")
public class TrainingCenterDatabase {
    @JacksonXmlElementWrapper(localName = "Courses")
    @JacksonXmlProperty(localName = "Course")
    private final List<Course> course = new ArrayList<>();

    public void addCourse(Course course) {
        this.course.add(course);
    }

    public List<Course> getCourse() {
        return course;
    }
}
