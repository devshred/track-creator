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
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

public class TrackCreator {
    public static final String COOKIE =
            "_ga=GA1.2.67…";
    private static final String SPREADSHEET_ID = "1trwAN0YqZUmUHpih8J-7N2u5NIDqv_lOZZ2vf2n-C7I";
    private static final String TOUR_DESCRIPTION = "Beispiel-Tour 2020";
    private static final String COPYRIGHT_AUTHOR = "Achim Autor";
    private static final String TOUR_PREFIX = "BT";
    private static final String OUTPUT_DIR = "out";
    private static final Person AUTHOR = Person.of("Achim",
            Email.of("achim@example.org"),
            Link.of("https://www.komoot.de/user/9999999999", "Achim", "KomootUserOnWeb"));

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        final List<List<Object>> rows = SheetReader.start(SPREADSHEET_ID);

        if (rows == null || rows.isEmpty()) {
            System.err.println("No data found.");
        } else {
            for (List row : rows) {
                if (isEmptyRow(row) || hasNoTracks(row)) continue;

                final String tag = (String) row.get(0);
                final Buffet buffet = Buffet.ofRow(row);
                final Hotel hotel = Hotel.ofRow(row);

                final String kurzBuffet = (String) row.get(7);
                final String langBuffet = (String) row.get(8);
                final String kurzPage = (String) row.get(14);
                final String langPage = (String) row.get(15);

                writeTrack(TOUR_PREFIX + tag, kurzPage, hotel, buffet, !StringUtils.isEmpty(kurzBuffet));
                writeTrack(TOUR_PREFIX + tag, langPage, hotel, buffet, !StringUtils.isEmpty(langBuffet));
            }
        }
    }

    private static boolean hasNoTracks(List row) {
        return row.size() < 15 || (StringUtils.isEmpty((String) row.get(14)) && StringUtils.isEmpty((String) row.get(15)));
    }

    private static boolean isEmptyRow(List row) {
        return StringUtils.isEmpty((String) row.get(2));
    }

    private static void writeTrack(String prefix, String page, Hotel hotel, Buffet buffet, boolean buffetAvailable) throws IOException {
        if (StringUtils.isEmpty(page)) return;

        final String tour = StringUtils.remove(page, "https://www.komoot.de/tour/");

        final HttpURLConnection con = (HttpURLConnection) new URL("https://www.komoot.de/api/v007/tours/" + tour + ".gpx").openConnection();
        con.setRequestMethod("GET");
        con.addRequestProperty("Cookie", COOKIE);

        final GPX gpxIn = GPX.read(con.getInputStream());
        con.disconnect();

        final Length length = gpxIn.tracks()
                .flatMap(Track::segments)
                .findFirst()
                .map(TrackSegment::points).orElse(Stream.empty())
                .collect(Geoid.WGS84.toPathLength());
        final int distance = Math.round((length.floatValue() / 1000));

        final String trackName = String.format("%s %03dkm", prefix, distance);
        final Link trackLink = Link.of(page, TOUR_DESCRIPTION + "; " + trackName, "trackOnWeb");

        final GPX.Builder builder = gpxIn.toBuilder()
                .metadata(
                        Metadata.builder()
                                .name(trackName)
                                .desc(TOUR_DESCRIPTION)
                                .author(AUTHOR)
                                .addLink(trackLink)
                                .copyright(Copyright.of(COPYRIGHT_AUTHOR, Calendar.getInstance().get(Calendar.YEAR)))
                                .build()
                );

        if (buffet != Buffet.EMPTY_BUFFET && buffetAvailable) {
            final WayPoint nearestToBuffet = findNearestTo(buffet, gpxIn);
            builder.addWayPoint(
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

        final WayPoint nearestToHotel = findNearestTo(hotel, gpxIn);
        builder.addWayPoint(
                WayPoint.builder()
                        .lat(nearestToHotel.getLatitude())
                        .lon(nearestToHotel.getLongitude())
                        .time(nearestToHotel.getTime().get())
                        .name(hotel.getName())
                        .sym("residence")
                        .type("Residence")
                        .build()
        )
                .trackFilter()
                .map(track -> track.toBuilder()
                        .name(trackName)
                        .addLink(trackLink)
                        .build())
                .build();

        final String gpxDir = OUTPUT_DIR + "/gpx/";
        new File(gpxDir).mkdir();
        final String filenameGpx = gpxDir + StringUtils.replace(trackName, " ", "_") + ".gpx";

        final GPX gpxOut = builder.build();
        GPX.writer(" ").write(gpxOut, filenameGpx);
        System.out.println("wrote file " + filenameGpx);
    }

    private static WayPoint findNearestTo(PointOfInterest poi, GPX gpxIn) {
        return gpxIn.tracks().findFirst().get().getSegments().get(0).getPoints().stream()
                .reduce((result, current) -> Geoid.WGS84.distance(current, poi.wayPoint()).intValue() < Geoid.WGS84.distance(result, poi.wayPoint()).intValue() ? current : result).get();
    }
}
