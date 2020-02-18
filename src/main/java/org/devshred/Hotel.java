package org.devshred;

import java.io.IOException;
import java.util.List;

public class Hotel extends PointOfInterest {
    private final String name;

    private Hotel(List row) throws IOException {
        super(Utils.getCoordinates((String) row.get(13)));
        name = (String) row.get(11);
    }

    static Hotel ofRow(List row) throws IOException {
        return new Hotel(row);
    }

    public String getName() {
        return name;
    }
}
