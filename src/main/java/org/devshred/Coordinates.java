package org.devshred;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Longitude;

public class Coordinates {
    private final Latitude latitude;
    private final Longitude longitude;

    public Coordinates(String latitude, String longitude) {
        this.latitude = Latitude.ofDegrees(Double.parseDouble(latitude));
        this.longitude = Longitude.ofDegrees(Double.parseDouble(longitude));
    }

    public Latitude getLatitude() {
        return latitude;
    }

    public Longitude getLongitude() {
        return longitude;
    }
}
