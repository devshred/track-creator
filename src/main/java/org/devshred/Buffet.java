package org.devshred;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

public class Buffet extends PointOfInterest {
    public static final Buffet EMPTY_BUFFET = new Buffet();

    private Buffet() {
        super();
    }

    private Buffet(List<String> row) throws IOException {
        super(Utils.getCoordinates(row.get(10)));
    }

    static Buffet ofRow(List<String> row) throws IOException {
        if (StringUtils.isEmpty(row.get(10))) return EMPTY_BUFFET;
        return new Buffet(row);
    }
}
