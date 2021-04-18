package org.devshred;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {
    private static final Pattern MAPS_COORDINATES_PATTERN = Pattern.compile("@(.*?),(.*?),");
    private static final Pattern MAPS_ISO_6709_PATTERN = Pattern.compile("place/(.*?)%C2%B0(.*?)'(.*?)%22N\\+(.*?)%C2%B0(.*?)'(.*?)%22E/@");

    static Coordinates getCoordinates(String buffetLink) throws IOException {
        final HttpURLConnection mapsLinks = (HttpURLConnection) new URL(buffetLink).openConnection();
        mapsLinks.setInstanceFollowRedirects(false);
        mapsLinks.connect();
        final String buffetLocation = mapsLinks.getHeaderField("Location");

        final Matcher isoMatcher = MAPS_ISO_6709_PATTERN.matcher(buffetLocation);
        if (isoMatcher.find()) {
            final int latDeg = Integer.parseInt(isoMatcher.group(1));
            final int latMin = Integer.parseInt(isoMatcher.group(2));
            final double latSec = Double.parseDouble(isoMatcher.group(3));
            final int longDeg = Integer.parseInt(isoMatcher.group(4));
            final int longMin = Integer.parseInt(isoMatcher.group(5));
            final double longSec = Double.parseDouble(isoMatcher.group(6));

            final double latitude = latDeg + (latMin * 60 + latSec)/3600;
            final double longitude = longDeg + (longMin * 60 + longSec)/3600;

            return new Coordinates(Double.toString(latitude), Double.toString(longitude));
        }

        final Matcher matcher = MAPS_COORDINATES_PATTERN.matcher(buffetLocation);
        if (!matcher.find()) throw new RuntimeException("coordinates not found");

        return new Coordinates(matcher.group(1), matcher.group(2));
    }
}
