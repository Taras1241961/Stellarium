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

    public MessierObject(int number, String name, String type, double raHours, double decDeg, double magnitude, String constellation) {
        this.number = number;
        this.name = name;
        this.type = type;
        this.ra = raHours * 15 * Math.PI / 180;
        this.dec = decDeg * Math.PI / 180;
        this.magnitude = magnitude;
        this.constellation = constellation;
    }

    public int getNumber() { return number; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getRa() { return ra; }
    public double getDec() { return dec; }
    public double getMagnitude() { return magnitude; }
    public String getConstellation() { return constellation; }

    public Color getColor() {
        switch(type) {
            case "Galaxy": return new Color(150, 150, 200);
            case "Nebula": return new Color(200, 150, 150);
            case "Cluster": return new Color(200, 200, 150);
            default: return Color.WHITE;
        }
    }
}