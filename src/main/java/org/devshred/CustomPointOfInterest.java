package org.devshred;

import java.io.IOException;

public class CustomPointOfInterest extends PointOfInterest{

    // Garmin TCX Types:
    // Generic
    // Summit
    // Valley
    // Water
    // Food
    // Danger
    // Left
    // Right
    // Straight
    // First Aid
    // 4th Category
    // 3rd Category
    // 2nd Category
    // 1st Category
    // Hors Category
    // Sprint

    private String name;
    private String type;

    private CustomPointOfInterest(){super();}

    public CustomPointOfInterest(String link) throws IOException {
        super(Utils.getCoordinates(link));
    }

    static CustomPointOfInterest ofLink(String link) throws IOException {
        return new CustomPointOfInterest(link);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getSym() {
        return type.toLowerCase();
    }
}
