package src.comcosmocom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StarData {

    @JsonProperty("type")
    private String type;

    private Geometry geometry;
    private Properties properties;

    public Geometry getGeometry() { return geometry; }
    public void setGeometry(Geometry geometry) { this.geometry = geometry; }

    public Properties getProperties() { return properties; }
    public void setProperties(Properties properties) { this.properties = properties; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geometry {
        @JsonProperty("type")
        private String type;

        @JsonProperty("coordinates")
        private double[] coordinates;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public double[] getCoordinates() { return coordinates; }
        public void setCoordinates(double[] coordinates) { this.coordinates = coordinates; }

        public double getRightAscensionRad() {
            if (coordinates == null || coordinates.length < 2) return 0;
            return coordinates[0] * (Math.PI / 12.0);
        }

        public double getDeclinationRad() {
            if (coordinates == null || coordinates.length < 2) return 0;
            return coordinates[1] * (Math.PI / 180.0);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        @JsonProperty("mag")
        private double magnitude;

        @JsonProperty("name")
        private String name;

        public double getMagnitude() { return magnitude; }
        public void setMagnitude(double magnitude) { this.magnitude = magnitude; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
