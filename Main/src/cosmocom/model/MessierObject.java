package src.cosmocom.model;

import java.awt.Color;

public class MessierObject {
    private int number;
    private String name;
    private String type;
    private double ra;
    private double dec;
    private double magnitude;
    private String constellation;
    private double distanceLy;
    private double sizeArcmin;

    public MessierObject(int number, String name, String type, double ra, double dec,
                         double magnitude, String constellation, double distanceLy, double sizeArcmin) {
        this.number = number;
        this.name = name;
        this.type = type;
        this.ra = ra;
        this.dec = dec;
        this.magnitude = magnitude;
        this.constellation = constellation;
        this.distanceLy = distanceLy;
        this.sizeArcmin = sizeArcmin;
    }

    public int getNumber() { return number; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getRa() { return ra; }
    public double getDec() { return dec; }
    public double getMagnitude() { return magnitude; }
    public String getConstellation() { return constellation; }
    public double getDistanceLy() { return distanceLy; }
    public double getSizeArcmin() { return sizeArcmin; }

    public Color getColor() {
        switch (type) {
            case "Open Cluster": return new Color(255, 200, 150);
            case "Globular Cluster": return new Color(255, 220, 100);
            case "Galaxy": return new Color(180, 200, 255);
            case "Nebula": return new Color(255, 150, 200);
            case "Planetary Nebula": return new Color(200, 255, 200);
            case "Supernova Remnant": return new Color(255, 100, 100);
            default: return Color.WHITE;
        }
    }
}