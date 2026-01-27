package comcosmocom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Игнорируем неизвестные поля, включая "id"
@JsonIgnoreProperties(ignoreUnknown = true)
public class StarData {

    @JsonProperty("type")
    private String type;

    @JsonProperty("id")
    private String id;  // ← ДОБАВЛЯЕМ ЭТО ПОЛЕ

    private Geometry geometry;
    private Properties properties;

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Geometry getGeometry() { return geometry; }
    public void setGeometry(Geometry geometry) { this.geometry = geometry; }

    public Properties getProperties() { return properties; }
    public void setProperties(Properties properties) { this.properties = properties; }

    // Вложенный класс Geometry (без изменений)
    public static class Geometry {
        @JsonProperty("type")
        private String type;

        @JsonProperty("coordinates")
        private double[] coordinates; // [RA в часах, Dec в градусах]

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public double[] getCoordinates() { return coordinates; }
        public void setCoordinates(double[] coordinates) { this.coordinates = coordinates; }

        public double getRightAscensionRad() {
            return coordinates[0] * (Math.PI / 12.0);
        }

        public double getDeclinationRad() {
            return coordinates[1] * (Math.PI / 180.0);
        }
    }

    // Вложенный класс Properties (без изменений)
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