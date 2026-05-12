package src.cosmocom.model;

public class MoonData {
    private String name;
    private double orbitRadiusKm;
    private double radiusKm;
    private double massKg;
    private double angle;
    private double orbitalPeriodDays;
    private double orbitalSpeed;

    public MoonData(String name, double orbitRadiusKm, double radiusKm, double massKg, double orbitalPeriodDays) {
        this.name = name;
        this.orbitRadiusKm = orbitRadiusKm;
        this.radiusKm = radiusKm;
        this.massKg = massKg;
        this.orbitalPeriodDays = orbitalPeriodDays;
        this.orbitalSpeed = (2 * Math.PI) / (orbitalPeriodDays * 24 * 3600);
        this.angle = Math.random() * 2 * Math.PI;
    }

    public String getName() { return name; }
    public double getOrbitRadiusKm() { return orbitRadiusKm; }
    public double getRadiusKm() { return radiusKm; }
    public double getMassKg() { return massKg; }
    public double getAngle() { return angle; }
    public double getOrbitalSpeed() { return orbitalSpeed; }
    public void setAngle(double angle) { this.angle = angle; }
    public double getOrbitalPeriodDays() { return orbitalPeriodDays; }
}