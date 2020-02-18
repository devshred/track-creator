package org.devshred;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {
    private static final Pattern MAPS_COORDINATES_PATTERN = Pattern.compile("@(.*?),(.*?),");

    static Coordinates getCoordinates(String buffetLink) throws IOException {
        final HttpURLConnection mapsLinks = (HttpURLConnection) new URL(buffetLink).openConnection();
        mapsLinks.setInstanceFollowRedirects(false);
        mapsLinks.connect();
        final String buffetLocation = mapsLinks.getHeaderField("Location");
        final Matcher matcher = MAPS_COORDINATES_PATTERN.matcher(buffetLocation);
        if (!matcher.find()) throw new RuntimeException("coordinates not found");
        return new Coordinates(matcher.group(1), matcher.group(2));
    }
}
