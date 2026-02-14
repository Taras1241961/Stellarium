package comcosmocom.model;

import java.awt.Color;

public class Star {
    private int id;
    private String name;
    private double rightAscension;
    private double declination;
    private double magnitude;
    private double colorIndex;

    public Star(int id, String name, double ra, double dec, double mag, double bv) {
        this.id = id;
        this.name = name;
        this.rightAscension = ra;
        this.declination = dec;
        this.magnitude = mag;
        this.colorIndex = bv;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getRightAscension() { return rightAscension; }
    public double getDeclination() { return declination; }
    public double getMagnitude() { return magnitude; }
    public double getColorIndex() { return colorIndex; }

    public Color getColor() {
        if (colorIndex < -0.2) return new Color(200, 220, 255);
        else if (colorIndex < 0.0) return new Color(255, 255, 255);
        else if (colorIndex < 0.3) return new Color(255, 255, 200);
        else if (colorIndex < 0.6) return new Color(255, 230, 150);
        else if (colorIndex < 1.0) return new Color(255, 200, 100);
        else return new Color(255, 150, 100);
    }
}
