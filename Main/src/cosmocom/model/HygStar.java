package src.cosmocom.model;

import java.awt.Color;

public class HygStar {
    private int id;
    private int hipId;
    private String name;
    private double ra;
    private double dec;
    private double mag;
    private double colorIndex;
    private double distance;
    private String spectralType;

    public HygStar(int id, int hipId, String name, double ra, double dec, double mag,
                   double colorIndex, double distance, String spectralType) {
        this.id = id;
        this.hipId = hipId;
        this.name = name;
        this.ra = ra;
        this.dec = dec;
        this.mag = mag;
        this.colorIndex = colorIndex;
        this.distance = distance;
        this.spectralType = spectralType;
    }

    public int getId() { return id; }
    public int getHipId() { return hipId; }
    public String getName() { return name; }
    public double getRa() { return ra; }
    public double getDec() { return dec; }
    public double getMag() { return mag; }
    public double getColorIndex() { return colorIndex; }
    public double getDistance() { return distance; }
    public String getSpectralType() { return spectralType; }

    public Color getColor() {
        if (colorIndex < -0.2) return new Color(200, 220, 255);
        else if (colorIndex < 0.0) return new Color(255, 255, 255);
        else if (colorIndex < 0.3) return new Color(255, 255, 200);
        else if (colorIndex < 0.6) return new Color(255, 230, 150);
        else if (colorIndex < 1.0) return new Color(255, 200, 100);
        else return new Color(255, 150, 100);
    }

    public int estimateTemperature() {
        if (spectralType != null && !spectralType.isEmpty()) {
            char type = spectralType.charAt(0);
            switch (type) {
                case 'O': return 40000;
                case 'B': return 20000;
                case 'A': return 8500;
                case 'F': return 6500;
                case 'G': return 5500;
                case 'K': return 4000;
                case 'M': return 3000;
            }
        }
        return (int)(4600 * (1.0 / (0.92 * colorIndex + 1.7) + 1.0 / (0.92 * colorIndex + 0.62)));
    }
}