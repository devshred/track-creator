package org.devshred;

import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.Email;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Person;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.devshred.tcx.TcxApp.writeTcx;

public class TrackCreator {
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        final String spreadsheetId = new Config().getProp("spreadsheetId");
        final List<List<Object>> rows = SheetReader.start(spreadsheetId);

        if (rows == null || rows.isEmpty()) {
            System.err.println("No data found.");
        } else if (isEtappenPlan(rows)) {
            for (List row : rows) {
                if (isEmptyRow(row) || hasNoTracks(row)) continue;

                final String tag = (String) row.get(0);
                final Buffet buffet = Buffet.ofRow(row);
                final Hotel hotel = Hotel.ofRow(row);

                final String kurzBuffet = (String) row.get(7);
                final String langBuffet = (String) row.get(8);
                final String kurzPage = (String) row.get(14);
                final String langPage = (String) row.get(15);

                final Set<CustomPointOfInterest> additionalWPkurz = additionalWayPoints(spreadsheetId, "WP" + tag + "K");
                final Set<CustomPointOfInterest> additionalWPlang = additionalWayPoints(spreadsheetId, "WP" + tag + "L");

                writeTrack(Config.INSTANCE.getProp("tourPrefix") + tag, kurzPage, hotel, buffet, !StringUtils.isEmpty(kurzBuffet), additionalWPkurz);
                writeTrack(Config.INSTANCE.getProp("tourPrefix") + tag, langPage, hotel, buffet, !StringUtils.isEmpty(langBuffet), additionalWPlang);
            }
        } else {
            final String trackName = rows.get(0).get(1).toString();
            final String trackDescription = rows.get(0).get(2).toString();
            final String komootLink = rows.get(1).get(1).toString();

            final List<CustomPointOfInterest> pointsOfInterest = new ArrayList<>();
            System.out.println(rows.size() + " rows found");
            for (List row : rows) {
                if (hasNoMapsLink(row)) continue;

                final CustomPointOfInterest poi = CustomPointOfInterest.ofLink(row.get(2).toString());
                poi.setName((String) row.get(0));
                poi.setType((String) row.get(2));

                pointsOfInterest.add(poi);
            }
            writeTrack(trackName, trackDescription, komootLink, pointsOfInterest);
        }
    }

    private static Set<CustomPointOfInterest> additionalWayPoints(String spreadsheetId, String sheetName) throws IOException, GeneralSecurityException {
        final List<List<Object>> rows = SheetReader.findCustomPointOfInterests(spreadsheetId, sheetName);
        final Set<CustomPointOfInterest> pointsOfInterest = new HashSet<>();

        for (List row : rows) {
            if (hasNoMapsLink(row)) continue;
            final CustomPointOfInterest poi = CustomPointOfInterest.ofLink(row.get(2).toString());
            poi.setName((String) row.get(1));
            poi.setType((String) row.get(0));

            pointsOfInterest.add(poi);
        }

        return pointsOfInterest;
    }

    private static boolean hasNoMapsLink(List row) {
        final String value = (String) row.get(2);
        return StringUtils.isEmpty(value) || !StringUtils.startsWith(value, "https://goo.gl/maps/");
    }

    private static boolean isEtappenPlan(List<List<Object>> rows) {
        return !rows.get(1).get(1).equals("Name");
    }

    private static boolean hasNoTracks(List row) {
        return row.size() < 15 || (StringUtils.isEmpty((String) row.get(14)) && StringUtils.isEmpty((String) row.get(15)));
    }

    private static boolean isEmptyRow(List row) {
        return row == null || row.size() < 3 || StringUtils.isEmpty((String) row.get(2));
    }

    private static void writeTrack(String prefix, String page, Hotel hotel, Buffet buffet, boolean buffetAvailable, Set<CustomPointOfInterest> additionalPOIs) throws IOException {
        if (StringUtils.isEmpty(page)) return;

        final String tour = StringUtils.remove(page, "https://www.komoot.de/tour/");

        final String komootCache = Config.INSTANCE.getProp("outputDir") + "/komoot/";
        final File cacheDir = new File(komootCache);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        final File fileCache = new File(komootCache + tour + ".gpx");

        if (!fileCache.exists()) {
            final HttpURLConnection con = (HttpURLConnection) new URL("https://www.komoot.de/api/v007/tours/" + tour + ".gpx").openConnection();
            con.setRequestMethod("GET");
            con.addRequestProperty("Cookie", Config.INSTANCE.getProp("komootCookie"));
            FileUtils.copyInputStreamToFile(con.getInputStream(), fileCache);
            con.disconnect();
        }

        final GPX gpxIn = GPX.read(FileUtils.openInputStream(fileCache));

        final Length length = gpxIn.tracks()
                .flatMap(Track::segments)
                .findFirst()
                .map(TrackSegment::points).orElse(Stream.empty())
                .collect(Geoid.WGS84.toPathLength());
        final int distance = Math.round((length.floatValue() / 1000));

        final String trackName = String.format("%s %03dkm", prefix, distance);
        final Link trackLink = Link.of(page, Config.INSTANCE.getProp("tourDescription") + "; " + trackName, "trackOnWeb");
        final Person author = Person.of(Config.INSTANCE.getProp("copyrightAuthor"),
                Email.of(Config.INSTANCE.getProp("autorEmail")),
                Link.of(Config.INSTANCE.getProp("autorLink"), Config.INSTANCE.getProp("copyrightAuthor"), "KomootUserOnWeb"));
        final GPX.Builder builder = gpxIn.toBuilder()
                .metadata(
                        Metadata.builder()
                                .name(trackName)
                                .desc(Config.INSTANCE.getProp("tourDescription"))
                                .author(author)
                                .addLink(trackLink)
                                .copyright(Copyright.of(Config.INSTANCE.getProp("copyrightAuthor"), Calendar.getInstance().get(Calendar.YEAR)))
                                .build()
                );

        final List<WayPoint> waypoints = new ArrayList<>();

        if (buffet != Buffet.EMPTY_BUFFET && buffetAvailable) {
            final WayPoint nearestToBuffet = findNearestTo(buffet, gpxIn);
            waypoints.add(
                    WayPoint.builder()
                            .lat(nearestToBuffet.getLatitude())
                            .lon(nearestToBuffet.getLongitude())
                            .time(nearestToBuffet.getTime().get())
                            .name("Buffet")
                            .sym("food")
                            .type("Food")
                            .build()
            );
        }

        for (CustomPointOfInterest poi : additionalPOIs) {
            final WayPoint nearestToPoi = findNearestTo(poi, gpxIn);
            waypoints.add(
                    WayPoint.builder()
                            .lat(nearestToPoi.getLatitude())
                            .lon(nearestToPoi.getLongitude())
                            .time(nearestToPoi.getTime().get())
                            .name(poi.getName())
                            .sym(poi.getSym())
                            .type(poi.getType())
                            .build()
            );
        }

        final WayPoint nearestToHotel = findNearestTo(hotel, gpxIn);
        waypoints.add(
                WayPoint.builder()
                        .lat(nearestToHotel.getLatitude())
                        .lon(nearestToHotel.getLongitude())
                        .time(nearestToHotel.getTime().get())
                        .name(hotel.getName())
                        .sym("residence")
                        .type("Residence")
                        .build()
        );

        waypoints.sort(Comparator.comparing(o -> o.getTime().get()));

        for (WayPoint wayPoint : waypoints) {
            builder.addWayPoint(wayPoint);
        }

        builder.trackFilter()
                .map(track -> track.toBuilder()
                        .name(trackName)
                        .addLink(trackLink)
                        .build())
                .build();

        final String gpxDir = Config.INSTANCE.getProp("outputDir") + "/gpx/";
        if (!new File(gpxDir).exists()) {
            new File(gpxDir).mkdir();
        }
        final String filenameGpx = gpxDir + StringUtils.replace(trackName, " ", "_") + ".gpx";

        final GPX gpxOut = builder.build();
        GPX.writer(" ").write(gpxOut, filenameGpx);
        System.out.println("wrote file " + filenameGpx);

        writeTcx(gpxOut, trackName);
    }

    private static void writeTrack(String trackName, String trackDescription, String komootLink, List<CustomPointOfInterest> pointsOfInterest) throws IOException {
        final String tour = StringUtils.remove(komootLink, "https://www.komoot.de/tour/");

        final String komootCache = Config.INSTANCE.getProp("outputDir") + "/komoot/";
        final File cacheDir = new File(komootCache);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        final File fileCache = new File(komootCache + tour + ".gpx");

        if (!fileCache.exists()) {
            final HttpURLConnection con = (HttpURLConnection) new URL("https://www.komoot.de/api/v007/tours/" + tour + ".gpx").openConnection();
            con.setRequestMethod("GET");
            con.addRequestProperty("Cookie", Config.INSTANCE.getProp("komootCookie"));
            FileUtils.copyInputStreamToFile(con.getInputStream(), fileCache);
            con.disconnect();
        }

        final GPX gpxIn = GPX.read(FileUtils.openInputStream(fileCache));

        final Length length = gpxIn.tracks()
                .flatMap(Track::segments)
                .findFirst()
                .map(TrackSegment::points).orElse(Stream.empty())
                .collect(Geoid.WGS84.toPathLength());
        final int distance = Math.round((length.floatValue() / 1000));

        final Link trackLink = Link.of(komootLink, trackDescription, "trackOnWeb");
        final Person author = Person.of(Config.INSTANCE.getProp("copyrightAuthor"),
                Email.of(Config.INSTANCE.getProp("autorEmail")),
                Link.of(Config.INSTANCE.getProp("autorLink"), Config.INSTANCE.getProp("copyrightAuthor"), "KomootUserOnWeb"));
        final GPX.Builder builder = gpxIn.toBuilder()
                .metadata(
                        Metadata.builder()
                                .name(trackName)
                                .desc(trackDescription)
                                .author(author)
                                .addLink(trackLink)
                                .copyright(Copyright.of(Config.INSTANCE.getProp("copyrightAuthor"), Calendar.getInstance().get(Calendar.YEAR)))
                                .build()
                );

        for (CustomPointOfInterest poi : pointsOfInterest) {
            System.out.println("adding waypoint");
            final WayPoint nearestToPoi = findNearestTo(poi, gpxIn);
            builder.addWayPoint(
                    WayPoint.builder()
                            .lat(nearestToPoi.getLatitude())
                            .lon(nearestToPoi.getLongitude())
                            .time(nearestToPoi.getTime().get())
                            .name(poi.getName())
                            .sym(poi.getSym())
                            .type(poi.getType())
                            .build()
            );
        }
        builder
                .trackFilter()
                .map(track -> track.toBuilder()
                        .name(trackName)
                        .addLink(trackLink)
                        .build())
                .build();

        final String gpxDir = Config.INSTANCE.getProp("outputDir") + "/gpx/";
        if (!new File(gpxDir).exists()) {
            new File(gpxDir).mkdir();
        }
        final String filenameGpx = gpxDir + StringUtils.replace(trackName, " ", "_") + ".gpx";

        final GPX gpxOut = builder.build();
        GPX.writer(" ").write(gpxOut, filenameGpx);
        System.out.println("wrote file " + filenameGpx);

        writeTcx(gpxOut, trackName);
    }

    private static WayPoint findNearestTo(PointOfInterest poi, GPX gpxIn) {
        return gpxIn.tracks().findFirst().get().getSegments().get(0).getPoints().stream()
                .reduce((result, current) -> Geoid.WGS84.distance(current, poi.wayPoint()).intValue() < Geoid.WGS84.distance(result, poi.wayPoint()).intValue() ? current : result).get();
    }
}
