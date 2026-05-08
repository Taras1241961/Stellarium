package src.cosmocom.model;

public class BoundaryPoint {
    private double ra;
    private double dec;
    private String constellation1;
    private String constellation2;

    public BoundaryPoint(double ra, double dec, String constellation1, String constellation2) {
        this.ra = ra;
        this.dec = dec;
        this.constellation1 = constellation1;
        this.constellation2 = constellation2;
    }

    public double getRa() { return ra; }
    public double getDec() { return dec; }
    public String getConstellation1() { return constellation1; }
    public String getConstellation2() { return constellation2; }

    public double[] getCartesianCoordinates(double radius) {
        double x = radius * Math.cos(dec) * Math.cos(ra);
        double y = radius * Math.cos(dec) * Math.sin(ra);
        double z = radius * Math.sin(dec);
        return new double[]{x, y, z};
    }
}



