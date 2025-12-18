package comcosmocom.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StarData {

    @JsonProperty("type")
    private String type;

    private Geometry geometry;
    private Properties properties;

    // Геттеры и сеттеры
    public Geometry getGeometry() { return geometry; }
    public void setGeometry(Geometry geometry) { this.geometry = geometry; }

    public Properties getProperties() { return properties; }
    public void setProperties(Properties properties) { this.properties = properties; }

    // Вложенный класс Geometry
    public static class Geometry {
        @JsonProperty("type")
        private String type;

        @JsonProperty("coordinates")
        private double[] coordinates; // [RA в часах, Dec в градусах]

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public double[] getCoordinates() { return coordinates; }
        public void setCoordinates(double[] coordinates) { this.coordinates = coordinates; }

        // Метод для получения RA в радианах
        public double getRightAscensionRad() {
            // RA в часах (0-24) → радианы (0-2π)
            return coordinates[0] * (Math.PI / 12.0);
        }

        // Метод для получения Dec в радианах
        public double getDeclinationRad() {
            // Dec в градусах (-90 до +90) → радианы (-π/2 до +π/2)
            return coordinates[1] * (Math.PI / 180.0);
        }
    }

    // Вложенный класс Properties
    public static class Properties {
        @JsonProperty("mag")
        private double magnitude; // звёздная величина

        @JsonProperty("name")
        private String name; // название (может быть null)

        public double getMagnitude() { return magnitude; }
        public void setMagnitude(double magnitude) { this.magnitude = magnitude; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}