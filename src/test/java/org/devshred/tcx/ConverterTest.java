package org.devshred.tcx;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Metadata;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

class ConverterTest {
private static final String TRACK_NAME = "trackName";

    @Test
    public void convertGpxToTcx() {
        final GPX gpx = GPX.builder()
                .metadata(
                        Metadata.builder().name(TRACK_NAME).build()
                )
                .addTrack(track -> track
                        .addSegment(segment -> segment
                                .addPoint(p -> p.lat(48).lon(16).ele(0).time(1_000))
                                .addPoint(p -> p.lat(49).lon(17).ele(0).time(2_000))
                                .addPoint(p -> p.lat(50).lon(18).ele(0).time(3_000))))
                .build();

        final TrainingCenterDatabase tcx = Converter.createTcxFromGpx(gpx);
        final Course course = tcx.getCourse().get(0);
        final Lap lap = course.getLap();

        assertThat(course.getName(), is(TRACK_NAME));
        assertThat(lap.getTotalTimeSeconds(), is(2.0));
        assertThat(lap.getDistanceMeters(), closeTo(265_956d, 500d));

        System.out.println(tcx);
    }
}