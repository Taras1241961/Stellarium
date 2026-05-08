package src.cosmocom.model;

import java.awt.Color;

public class PlanetData {
    private String name;
    private double semiMajorAxis;
    private double eccentricity;
    private double inclination;
    private double orbitalPeriod;
    private double radius;
    private double albedo;
    private Color color;
    private double angularSizeArcsec;
    private boolean isDwarf;

    private double ra;
    private double dec;
    private double distanceAU;
    private double eclipticLon;
    private double eclipticLat;

    public PlanetData(String name, double semiMajorAxis, double eccentricity,
                      double inclination, double orbitalPeriod, double radius,
                      Color color, double maxAngularSize, boolean isDwarf) {
        this.name = name;
        this.semiMajorAxis = semiMajorAxis;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.orbitalPeriod = orbitalPeriod;
        this.radius = radius;
        this.albedo = 0.0;
        this.color = color;
        this.angularSizeArcsec = maxAngularSize;
        this.isDwarf = isDwarf;
    }

    public String getName() { return name; }
    public double getSemiMajorAxis() { return semiMajorAxis; }
    public double getEccentricity() { return eccentricity; }
    public double getInclination() { return inclination; }
    public double getOrbitalPeriod() { return orbitalPeriod; }
    public double getRadius() { return radius; }
    public double getAlbedo() { return albedo; }
    public Color getColor() { return color; }
    public double getAngularSizeArcsec() { return angularSizeArcsec; }
    public boolean isDwarf() { return isDwarf; }

    public double getRa() { return ra; }
    public double getDec() { return dec; }
    public double getDistanceAU() { return distanceAU; }
    public double getEclipticLon() { return eclipticLon; }
    public double getEclipticLat() { return eclipticLat; }

    public void setRa(double ra) { this.ra = ra; }
    public void setDec(double dec) { this.dec = dec; }
    public void setDistanceAU(double distance) { this.distanceAU = distance; }
    public void setEclipticLon(double lon) { this.eclipticLon = lon; }
    public void setEclipticLat(double lat) { this.eclipticLat = lat; }

    public double getPhase(double jd, double sunRa) {
        double elong = Math.abs(ra - sunRa);
        if (elong > Math.PI) elong = 2 * Math.PI - elong;
        return (1 + Math.cos(elong)) / 2;
    }
}