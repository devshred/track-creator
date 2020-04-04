package org.devshred.tcx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;

import static org.devshred.tcx.TcxApp.XML_MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

public class TrainingCenterDatabaseTest {
    @Test
    public void mapTrackToTcx() throws JsonProcessingException {
        final TrainingCenterDatabase trainingCenterDatabase = new TrainingCenterDatabase();
        final Course course = new Course("A1 080km 710Hm");
        final Lap lap = new Lap();

        lap.setTotalTimeSeconds(28771d);
        lap.setDistanceMeters(79920.05133152752);
        lap.setBeginPosition(new Position(36.72100501, -4.41088201));
        lap.setEndPosition(new Position(36.73361891, -3.68807099));
        lap.setIntensity("Active");
        course.setLap(lap);

        final Track track = new Track();
        track.addTrackpoint(new Trackpoint(
                LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0, 0),
                new Position(36.72100501, -4.41088201),
                14d,
                0d
        ));
        track.addTrackpoint(new Trackpoint(
                LocalDateTime.of(2010, Month.JANUARY, 1, 3, 16, 4),
                new Position(36.74881701, -4.07262399),
                3d,
                32678.529
        ));
        track.addTrackpoint(new Trackpoint(
                LocalDateTime.of(2010, Month.JANUARY, 1, 7, 59, 31),
                new Position(36.73361891, -3.68807099),
                11d,
                79920.051
        ));
        course.setTrack(track);

        course.addCoursePoint(new CoursePoint(
                "Buffet",
                LocalDateTime.of(2010, Month.JANUARY, 1, 3, 16, 4),
                new Position(36.74881701, -4.07262399),
                "Food"));

        trainingCenterDatabase.addCourse(course);

        final String actualTcx = XML_MAPPER.writeValueAsString(trainingCenterDatabase);

        System.out.println(actualTcx);
        final File expectedTcx = new File(getClass().getClassLoader().getResource("testsource.tcx").getFile());
        assertThat(actualTcx, isIdenticalTo(expectedTcx).ignoreWhitespace());
    }
}