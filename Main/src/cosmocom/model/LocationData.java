package src.cosmocom.model;

import java.util.ArrayList;
import java.util.List;

public class LocationData {
    private String planetName;
    private String locationName;
    private double latitude;
    private double longitude;
    private double altitude;
    private String description;

    public LocationData(String planetName, String locationName,
                        double latitude, double longitude,
                        double altitude, String description) {
        this.planetName = planetName;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.description = description;
    }

    public String getPlanetName() { return planetName; }
    public String getLocationName() { return locationName; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getAltitude() { return altitude; }
    public String getDescription() { return description; }

    public static List<LocationData> getAllLocations() {
        List<LocationData> locations = new ArrayList<>();

        // Земля - Эльбрус
        locations.add(new LocationData("Earth", "Mount Elbrus",
                43.3499, 42.4453, 5.64,
                "Highest peak in Europe, dormant volcano"));

        // Марс - Олимп
        locations.add(new LocationData("Mars", "Olympus Mons",
                18.65, 226.2, 21.23,
                "Highest volcano in Solar System"));

        // Венера - Максвелла
        locations.add(new LocationData("Venus", "Maxwell Montes",
                65.2, 3.3, 11.0,
                "Highest point on Venus, metal snow"));

        // Меркурий - Равнина Жары
        locations.add(new LocationData("Mercury", "Caloris Planitia",
                32.57, 162.31, -2.0,
                "Giant impact basin, extreme temperatures"));

        return locations;
    }

    public static LocationData getLocation(String planetName, String locationName) {
        for (LocationData loc : getAllLocations()) {
            if (loc.getPlanetName().equalsIgnoreCase(planetName) &&
                    loc.getLocationName().equalsIgnoreCase(locationName)) {
                return loc;
            }
        }
        return null;
    }
}