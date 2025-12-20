package comcosmocom.model;

public class Star {
    private int id;
    private String name; // Добавляем поле для имени
    private double rightAscension;  // в радианах
    private double declination;     // в радианах
    private double magnitude;
    private double colorIndex;

    // Конструктор с именем
    public Star(int id, double ra, double dec, double mag, double bv) {
        this(id, null, ra, dec, mag, bv);
    }

    public Star(int id, String name, double ra, double dec, double mag, double bv) {
        this.id = id;
        this.name = name;
        this.rightAscension = ra;
        this.declination = dec;
        this.magnitude = mag;
        this.colorIndex = bv;
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public double getRightAscension() { return rightAscension; }
    public double getDeclination() { return declination; }
    public double getMagnitude() { return magnitude; }
    public double getColorIndex() { return colorIndex; }

    // Получаем RA в часах (для отладки)
    public double getRightAscensionHours() {
        return rightAscension * (12.0 / Math.PI);
    }

    // Получаем Dec в градусах (для отладки)
    public double getDeclinationDegrees() {
        return declination * (180.0 / Math.PI);
    }

    // Метод для преобразования сферических координат в декартовы
    public double[] getCartesianCoordinates(double sphereRadius) {
        // Преобразуем сферические координаты (RA, Dec) в декартовы (x, y, z)
        double longitude = rightAscension;
        double latitude = declination;

        double x = sphereRadius * Math.cos(latitude) * Math.cos(longitude);
        double y = sphereRadius * Math.cos(latitude) * Math.sin(longitude);
        double z = sphereRadius * Math.sin(latitude);

        return new double[]{x, y, z};
    }

    // Метод для определения размера звезды на экране (основано на звёздной величине)
    public double getApparentSize(double baseSize) {
        return baseSize * Math.pow(10, -magnitude / 5.0);
    }

    // Метод для определения цвета звезды по B-V индексу
    public java.awt.Color getColor() {
        if (colorIndex < -0.5) return java.awt.Color.CYAN;
        else if (colorIndex < 0.0) return java.awt.Color.WHITE;
        else if (colorIndex < 0.3) return new java.awt.Color(255, 255, 200);
        else if (colorIndex < 0.6) return new java.awt.Color(255, 230, 150);
        else if (colorIndex < 1.0) return new java.awt.Color(255, 200, 100);
        else return new java.awt.Color(255, 150, 100);
    }
}
