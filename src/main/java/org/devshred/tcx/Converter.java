package org.devshred.tcx;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

public final class Converter {
    private Converter() {
    }

    public static TrainingCenterDatabase createTcxFromGpx(GPX gpx) {
        final List<WayPoint> gpxPoints = gpx.getTracks().get(0).getSegments().get(0).getPoints();
        final Length length = calculateLength(gpx);

        final TrainingCenterDatabase trainingCenterDatabase = new TrainingCenterDatabase();
        final Course course = new Course(gpx.getMetadata().get().getName().get());
        final Lap lap = new Lap();

        lap.setTotalTimeSeconds((double) ChronoUnit.SECONDS.between(gpxPoints.get(0).getTime().get(), gpxPoints.get(gpxPoints.size() - 1).getTime().get()));
        lap.setDistanceMeters(length.doubleValue());
        lap.setBeginPosition(new Position(gpxPoints.get(0).getLatitude().toDegrees(), gpxPoints.get(0).getLongitude().toDegrees()));
        lap.setEndPosition(new Position(gpxPoints.get(gpxPoints.size() - 1).getLatitude().toDegrees(), gpxPoints.get(gpxPoints.size() - 1).getLongitude().toDegrees()));
        lap.setIntensity("Active");
        course.setLap(lap);

        final Track track = new Track();
        double distance = 0d;
        WayPoint previous = null;
        for (WayPoint point : gpxPoints) {
            if (previous != null) {
                distance += Geoid.WGS84.distance(previous, point).doubleValue();
                track.addTrackpoint(new Trackpoint(
                        point.getTime().get().toLocalDateTime(),
                        new Position(point.getLatitude().toDegrees(), point.getLongitude().toDegrees()),
                        point.getElevation().get().doubleValue(),
                        distance
                ));
            }
            previous = point;
        }
        course.setTrack(track);

        gpx.getWayPoints().forEach(
                wayPoint -> course.addCoursePoint(new CoursePoint(
                        wayPoint.getName().get(),
                        wayPoint.getTime().get().toLocalDateTime(),
                        new Position(wayPoint.getLatitude().doubleValue(), wayPoint.getLongitude().doubleValue()),
                        wayPoint.getType().get())));

        trainingCenterDatabase.addCourse(course);
        return trainingCenterDatabase;
    }

    private static Length calculateLength(GPX gpx) {
        return gpx.tracks()
                .flatMap(io.jenetics.jpx.Track::segments)
                .findFirst()
                .map(TrackSegment::points).orElse(Stream.empty())
                .collect(Geoid.WGS84.toPathLength());
    }
}
