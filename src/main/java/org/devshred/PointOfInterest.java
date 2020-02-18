package org.devshred;

import io.jenetics.jpx.WayPoint;

public abstract class PointOfInterest {
    private Coordinates coordinates;

    public PointOfInterest() {
    }

    public PointOfInterest(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public WayPoint wayPoint() {
        return WayPoint.of(coordinates.getLatitude(), coordinates.getLongitude());
    }
}
