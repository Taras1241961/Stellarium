package src.cosmocom.gui;

import src.cosmocom.data.*;
import src.cosmocom.model.*;
import src.cosmocom.utils.TimeUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class StarSpherePanel extends JPanel {
    private List<HygStar> stars;
    private List<MessierObject> messierObjects;
    private List<int[]> constellationLines = new ArrayList<>();
    private List<BoundaryPoint> boundaryPoints = new ArrayList<>();
    private Map<String, String> constellationNames = new HashMap<>();
    private Map<HygStar, double[]> starPositions = new HashMap<>();
    private Map<MessierObject, double[]> messierPositions = new HashMap<>();

    private List<PlanetData> planets;
    private Map<PlanetData, double[]> planetScreenPositions = new HashMap<>();
    private boolean showPlanets = true;

    private double viewAngleX = 0.0, viewAngleY = 90.0, fieldOfView = 60.0;
    private int lastMouseX, lastMouseY;

    private boolean showGrid = true, showConstellations = true, showLabels = true;
    private boolean showMessier = true, showBoundaries = true;
    private boolean showSun = true, showMoon = true, showEcliptic = true;
    private boolean invertRA = true;

    private String selectedObjectName = null;
    private javax.swing.Timer messageTimer;

    private double timeOffsetDays = 0;
    private boolean isTimePlaying = false;
    private double timeSpeed = 1.0;

    private double sunRa = 0, sunDec = 0;
    private double moonRa = 0, moonDec = 0;
    private double moonIllumination = 0;

    private List<SearchableObject> searchableObjects;
    private List<SearchableObject> searchResults;
    private SearchableObject selectedSearchResult = null;

    private static class SearchableObject {
        String name;
        String type;
        double ra;
        double dec;
        Object source;

        SearchableObject(String name, String type, double ra, double dec, Object source) {
            this.name = name;
            this.type = type;
            this.ra = ra;
            this.dec = dec;
            this.source = source;
        }

        String getDisplayName() {
            return type + ": " + name;
        }
    }

    public StarSpherePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1200, 900));

        loadAstronomicalData();
        initPlanets();
        initSearchableObjects();
        setupMouseControls();
        setupKeyboardControls();
        setupTimeTimer();
        setFocusable(true);

        messageTimer = new javax.swing.Timer(3000, e -> {
            selectedObjectName = null;
            repaint();
        });
        messageTimer.setRepeats(false);
    }

    private void initPlanets() {
        planets = new ArrayList<>();

        planets.add(new PlanetData("Mercury", 0.387, 0.2056, 7.0, 0.2408, 2440, new Color(180, 160, 140), 5.0, false));
        planets.add(new PlanetData("Venus", 0.723, 0.0068, 3.4, 0.6152, 6052, new Color(230, 210, 150), 8.0, false));
        planets.add(new PlanetData("Mars", 1.524, 0.0934, 1.9, 1.8808, 3390, new Color(200, 120, 80), 4.0, false));
        planets.add(new PlanetData("Jupiter", 5.203, 0.0484, 1.3, 11.862, 69911, new Color(210, 180, 140), 35.0, false));
        planets.add(new PlanetData("Saturn", 9.537, 0.0542, 2.5, 29.457, 58232, new Color(230, 210, 160), 30.0, false));
        planets.add(new PlanetData("Uranus", 19.191, 0.0472, 0.8, 84.020, 25362, new Color(180, 220, 230), 20.0, false));
        planets.add(new PlanetData("Neptune", 30.069, 0.0086, 1.8, 164.8, 24622, new Color(80, 100, 180), 19.0, false));

        planets.add(new PlanetData("Pluto", 39.482, 0.2488, 17.1, 247.94, 1188, new Color(200, 180, 150), 1.5, true));
        planets.add(new PlanetData("Ceres", 2.766, 0.079, 10.6, 4.60, 473, new Color(200, 190, 170), 1.0, true));
        planets.add(new PlanetData("Eris", 67.78, 0.436, 44.0, 557.0, 1163, new Color(180, 170, 160), 1.0, true));
        planets.add(new PlanetData("Makemake", 45.79, 0.159, 29.0, 309.0, 715, new Color(190, 180, 170), 0.8, true));
        planets.add(new PlanetData("Haumea", 43.13, 0.195, 28.2, 285.4, 620, new Color(200, 190, 180), 0.8, true));
    }

    private void loadAstronomicalData() {
        try {
            HygLoader hygLoader = new HygLoader();
            hygLoader.loadHygData("data/hygdata_v3.csv");
            stars = hygLoader.getStars();

            constellationLines = ConstellationLineLoader.loadLines("data/constellationship.fab", hygLoader.getHipMap());
            boundaryPoints = ConstellationBoundaryLoader.loadBoundaries("data/constbnd.dat.txt");
            messierObjects = MessierLoader.loadMessier("data/messier.txt");
            loadConstellationNames("data/constellation_names.txt");

            System.out.println("Loaded: stars=" + stars.size() + ", lines=" + constellationLines.size() +
                    ", boundaries=" + boundaryPoints.size() + ", messier=" + messierObjects.size());

        } catch (Exception e) {
            e.printStackTrace();
            stars = new ArrayList<>();
            messierObjects = new ArrayList<>();
        }
    }

    private void initSearchableObjects() {
        searchableObjects = new ArrayList<>();

        searchableObjects.add(new SearchableObject("Sun", "Star", sunRa, sunDec, null));
        searchableObjects.add(new SearchableObject("Moon", "Moon", moonRa, moonDec, null));

        if (stars != null) {
            for (HygStar star : stars) {
                if (star.getName() != null && !star.getName().isEmpty()) {
                    searchableObjects.add(new SearchableObject(
                            star.getName(), "Star", star.getRa(), star.getDec(), star));
                }
            }
        }

        if (planets != null) {
            for (PlanetData planet : planets) {
                searchableObjects.add(new SearchableObject(
                        planet.getName(), "Planet", planet.getRa(), planet.getDec(), planet));
            }
        }

        if (messierObjects != null) {
            for (MessierObject obj : messierObjects) {
                searchableObjects.add(new SearchableObject(
                        "M" + obj.getNumber() + " " + obj.getName(), "Messier",
                        obj.getRa(), obj.getDec(), obj));
            }
        }

        System.out.println("Searchable objects: " + searchableObjects.size());
    }

    private List<SearchableObject> searchObjects(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerSearch = searchText.toLowerCase().trim();
        List<SearchableObject> results = new ArrayList<>();

        for (SearchableObject obj : searchableObjects) {
            if (obj.name.toLowerCase().contains(lowerSearch)) {
                results.add(obj);
            }
        }

        results.sort((a, b) -> {
            boolean aExact = a.name.equalsIgnoreCase(lowerSearch);
            boolean bExact = b.name.equalsIgnoreCase(lowerSearch);
            if (aExact && !bExact) return -1;
            if (!aExact && bExact) return 1;
            return a.name.compareTo(b.name);
        });

        return results;
    }

    public void search(String searchText) {
        updateSunPosition();
        updateMoonPosition();
        updatePlanetPositions();

        for (SearchableObject obj : searchableObjects) {
            if (obj.name.equals("Sun")) {
                obj.ra = sunRa;
                obj.dec = sunDec;
            } else if (obj.name.equals("Moon")) {
                obj.ra = moonRa;
                obj.dec = moonDec;
            } else if (obj.type.equals("Planet") && obj.source != null) {
                PlanetData planet = (PlanetData) obj.source;
                obj.ra = planet.getRa();
                obj.dec = planet.getDec();
            } else if (obj.type.equals("Star") && obj.source != null) {
                HygStar star = (HygStar) obj.source;
                obj.ra = star.getRa();
                obj.dec = star.getDec();
            } else if (obj.type.equals("Messier") && obj.source != null) {
                MessierObject mess = (MessierObject) obj.source;
                obj.ra = mess.getRa();
                obj.dec = mess.getDec();
            }
        }

        this.searchResults = searchObjects(searchText);
        if (!searchResults.isEmpty()) {
            selectSearchResult(0);
        } else {
            selectedSearchResult = null;
            selectedObjectName = "Not found: " + searchText;
            if (messageTimer.isRunning()) messageTimer.stop();
            messageTimer.start();
            repaint();
        }
    }

    public void selectSearchResult(int index) {
        if (searchResults != null && index >= 0 && index < searchResults.size()) {
            selectedSearchResult = searchResults.get(index);

            if (selectedSearchResult.type.equals("Star") && selectedSearchResult.source != null) {
                HygStar star = (HygStar) selectedSearchResult.source;
                selectedSearchResult.ra = star.getRa();
                selectedSearchResult.dec = star.getDec();
            } else if (selectedSearchResult.type.equals("Planet") && selectedSearchResult.source != null) {
                PlanetData planet = (PlanetData) selectedSearchResult.source;
                selectedSearchResult.ra = planet.getRa();
                selectedSearchResult.dec = planet.getDec();
            } else if (selectedSearchResult.type.equals("Messier") && selectedSearchResult.source != null) {
                MessierObject obj = (MessierObject) selectedSearchResult.source;
                selectedSearchResult.ra = obj.getRa();
                selectedSearchResult.dec = obj.getDec();
            } else if (selectedSearchResult.name.equals("Sun")) {
                selectedSearchResult.ra = sunRa;
                selectedSearchResult.dec = sunDec;
            } else if (selectedSearchResult.name.equals("Moon")) {
                selectedSearchResult.ra = moonRa;
                selectedSearchResult.dec = moonDec;
            }

            double ra = 2 * Math.PI - selectedSearchResult.ra;
            if (ra >= 2 * Math.PI) ra -= 2 * Math.PI;
            double dec = selectedSearchResult.dec;

            double x = Math.cos(dec) * Math.cos(ra);
            double y = Math.cos(dec) * Math.sin(ra);
            double z = Math.sin(dec);

            double r = Math.sqrt(x*x + z*z);
            double viewYTarget = Math.atan2(y, r);
            double viewXTarget = Math.atan2(x, z);

            viewAngleY = Math.toDegrees(viewYTarget);
            viewAngleX = Math.toDegrees(viewXTarget);

            repaint();

            selectedObjectName = String.format("Found: %s (RA: %.2f h, Dec: %.2f°)",
                    selectedSearchResult.getDisplayName(),
                    selectedSearchResult.ra * 12 / Math.PI,
                    Math.toDegrees(selectedSearchResult.dec));
            if (messageTimer.isRunning()) messageTimer.stop();
            messageTimer.start();
            repaint();
        }
    }

    public void nextSearchResult() {
        if (searchResults != null && !searchResults.isEmpty()) {
            int currentIndex = searchResults.indexOf(selectedSearchResult);
            int nextIndex = (currentIndex + 1) % searchResults.size();
            selectSearchResult(nextIndex);
        }
    }

    public void previousSearchResult() {
        if (searchResults != null && !searchResults.isEmpty()) {
            int currentIndex = searchResults.indexOf(selectedSearchResult);
            int prevIndex = (currentIndex - 1 + searchResults.size()) % searchResults.size();
            selectSearchResult(prevIndex);
        }
    }

    private void loadConstellationNames(String filename) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                constellationNames.put(parts[0].trim(), parts[1].trim());
            }
        }
    }

    private void setupMouseControls() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                checkObjectSelection(e.getX(), e.getY());
            }
            public void mouseReleased(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;
                viewAngleX += dx * 0.5;
                viewAngleY -= dy * 0.5;
                if (viewAngleY > 90) viewAngleY = 90;
                if (viewAngleY < -90) viewAngleY = -90;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                repaint();
            }
        });

        addMouseWheelListener(e -> {
            fieldOfView += e.getWheelRotation() * 3;
            fieldOfView = Math.max(20, Math.min(120, fieldOfView));
            repaint();
        });
    }

    private void setupKeyboardControls() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: viewAngleX -= 5; break;
                    case KeyEvent.VK_RIGHT: viewAngleX += 5; break;
                    case KeyEvent.VK_UP: viewAngleY += 5; break;
                    case KeyEvent.VK_DOWN: viewAngleY -= 5; break;
                    case KeyEvent.VK_SPACE: viewAngleX = 0; viewAngleY = 90; fieldOfView = 60; break;
                    case KeyEvent.VK_I: break;
                }
            }
            return false;
        });
    }

    private void setupTimeTimer() {
        javax.swing.Timer timeTimer = new javax.swing.Timer(50, e -> {
            if (isTimePlaying) {
                timeOffsetDays += timeSpeed / 20.0;
                repaint();
            }
        });
        timeTimer.start();
    }

    public void timeRewindFast() {
        timeSpeed = -Math.abs(timeSpeed) * 2;
        if (Math.abs(timeSpeed) > 1000) timeSpeed = -1000;
        if (!isTimePlaying) isTimePlaying = true;
    }

    public void timeRewind() {
        timeSpeed = -Math.abs(timeSpeed);
        if (!isTimePlaying) isTimePlaying = true;
    }

    public void timePlayPause() {
        isTimePlaying = !isTimePlaying;
    }

    public void timeForward() {
        timeSpeed = Math.abs(timeSpeed);
        if (!isTimePlaying) isTimePlaying = true;
    }

    public void timeForwardFast() {
        timeSpeed = Math.abs(timeSpeed) * 2;
        if (timeSpeed > 1000) timeSpeed = 1000;
        if (!isTimePlaying) isTimePlaying = true;
    }

    public void timeReset() {
        timeOffsetDays = 0;
        timeSpeed = 1.0;
        isTimePlaying = false;
        repaint();
    }

    public double getTimeOffsetDays() { return timeOffsetDays; }
    public double getTimeSpeed() { return Math.abs(timeSpeed); }
    public boolean isTimePlaying() { return isTimePlaying; }
    public void setInvertRA(boolean invert) { }
    public boolean isInvertRA() { return invertRA; }

    private double getCurrentJD() {
        return TimeUtils.getCurrentJD() + timeOffsetDays;
    }

    private void updateSunPosition() {
        double jd = getCurrentJD();
        double T = (jd - 2451545.0) / 36525.0;

        double L0 = 280.46646 + 36000.76983 * T + 0.0003032 * T * T;
        double M = 357.52911 + 35999.05029 * T - 0.0001537 * T * T;

        double C = (1.914602 - 0.004817 * T - 0.000014 * T * T) * Math.sin(Math.toRadians(M));
        C += (0.019993 - 0.000101 * T) * Math.sin(Math.toRadians(2 * M));
        C += 0.000289 * Math.sin(Math.toRadians(3 * M));

        double sunLon = L0 + C;
        double epsilon = 23.439291 - 0.013004 * T;

        double sunLonRad = Math.toRadians(sunLon);
        double epsilonRad = Math.toRadians(epsilon);

        sunRa = Math.atan2(Math.cos(epsilonRad) * Math.sin(sunLonRad), Math.cos(sunLonRad));
        if (sunRa < 0) sunRa += 2 * Math.PI;
        sunDec = Math.asin(Math.sin(epsilonRad) * Math.sin(sunLonRad));
    }

    private void updateMoonPosition() {
        double jd = getCurrentJD();
        double T = (jd - 2451545.0) / 36525.0;

        double Lm = 218.3165 + 481267.8813 * T;
        double Mm = 134.9629 + 477198.8676 * T;
        double Nm = 125.0445 - 1934.1363 * T;
        double D = 297.8502 + 445267.1115 * T;

        double Ec = 6.2888 * Math.sin(Math.toRadians(Mm));

        double moonLon = Lm + Ec + 1.274 * Math.sin(Math.toRadians(2 * D - Mm)) +
                0.658 * Math.sin(Math.toRadians(2 * D));

        double moonLat = 5.145 * Math.sin(Math.toRadians(Nm));
        double epsilon = 23.439291 - 0.013004 * T;

        double moonLonRad = Math.toRadians(moonLon);
        double moonLatRad = Math.toRadians(moonLat);
        double epsilonRad = Math.toRadians(epsilon);

        moonRa = Math.atan2(Math.sin(moonLonRad) * Math.cos(epsilonRad) -
                        Math.tan(moonLatRad) * Math.sin(epsilonRad),
                Math.cos(moonLonRad));
        if (moonRa < 0) moonRa += 2 * Math.PI;

        moonDec = Math.asin(Math.sin(moonLatRad) * Math.cos(epsilonRad) +
                Math.cos(moonLatRad) * Math.sin(epsilonRad) * Math.sin(moonLonRad));

        double moonSunElongation = Math.acos(Math.sin(moonDec) * Math.sin(sunDec) +
                Math.cos(moonDec) * Math.cos(sunDec) *
                        Math.cos(moonRa - sunRa));
        moonIllumination = (1 - Math.cos(moonSunElongation)) / 2;
    }

    private void updatePlanetPositions() {
        double jd = getCurrentJD();

        updateSunPosition();

        double earthPeriod = 365.25;
        double earthDays = (jd - 2451545.0) % earthPeriod;
        double earthAngle = 2 * Math.PI * earthDays / earthPeriod;
        double r0 = 1.0;
        double v0 = 29.8;

        double earthX = r0 * Math.cos(earthAngle);
        double earthY = r0 * Math.sin(earthAngle);

        for (PlanetData planet : planets) {
            String name = planet.getName();

            if (name.equals("Mercury") || name.equals("Venus")) {
                double r, v, BmaxRad, synodicPeriodDays;

                if (name.equals("Mercury")) {
                    r = 0.387;
                    v = 47.9;
                    BmaxRad = Math.asin(r / r0);
                    synodicPeriodDays = 116.0;
                } else {
                    r = 0.723;
                    v = 35.0;
                    BmaxRad = Math.asin(r / r0);
                    synodicPeriodDays = 584.0;
                }

                double days = jd - 2451545.0;
                double phase = (days % synodicPeriodDays) / synodicPeriodDays;

                double t = phase * 2 * Math.PI;
                double B = BmaxRad * Math.sin(t);

                double sign = (phase < 0.5) ? 1.0 : -1.0;
                B = sign * Math.abs(B);

                double dB = r0 * Math.cos(Math.abs(B)) + Math.sqrt(r * r - r0 * r0 * Math.sin(Math.abs(B)) * Math.sin(Math.abs(B)));

                double cosPhi = (r0 - dB * Math.cos(Math.abs(B))) / r;
                double sinPhi = (dB * Math.sin(Math.abs(B))) / r;

                if (B < 0) sinPhi = -sinPhi;

                double Vx = v * cosPhi - v0;
                double Vy = v * sinPhi;

                double a_angle = Math.atan2(Vx, Vy);
                double U = a_angle + B - Math.PI / 2;
                double speed = Math.sqrt(Vx * Vx + Vy * Vy);
                double w = (speed * Math.abs(Math.sin(U))) / dB;
                double direction = (U > 0) ? 1.0 : -1.0;

                double deltaB = direction * w * 0.01;

                double sunLon = sunRa;
                double lambda = sunLon + B;
                if (lambda < 0) lambda += 2 * Math.PI;
                if (lambda >= 2 * Math.PI) lambda -= 2 * Math.PI;

                planet.setEclipticLon(lambda);
                planet.setDistanceAU(dB);

                double epsilon = Math.toRadians(23.44);
                double beta = 0;
                double ra = Math.atan2(Math.sin(lambda) * Math.cos(epsilon) - Math.tan(beta) * Math.sin(epsilon),
                        Math.cos(lambda));
                double dec = Math.asin(Math.sin(beta) * Math.cos(epsilon) +
                        Math.cos(beta) * Math.sin(epsilon) * Math.sin(lambda));
                if (ra < 0) ra += 2 * Math.PI;

                planet.setRa(ra);
                planet.setDec(dec);

            } else {
                double a = planet.getSemiMajorAxis();
                double e = planet.getEccentricity();
                double orbitalPeriod = planet.getOrbitalPeriod() * 365.25;

                double days = (jd - 2451545.0) % orbitalPeriod;
                double meanAnomaly = 2 * Math.PI * days / orbitalPeriod;

                double E = meanAnomaly;
                for (int iter = 0; iter < 10; iter++) {
                    double delta = E - e * Math.sin(E) - meanAnomaly;
                    if (Math.abs(delta) < 1e-8) break;
                    E = E - delta / (1 - e * Math.cos(E));
                }

                double trueAnomaly = 2 * Math.atan2(Math.sqrt(1 + e) * Math.sin(E / 2),
                        Math.sqrt(1 - e) * Math.cos(E / 2));
                double r = a * (1 - e * Math.cos(E));
                double v = 29.8 / Math.sqrt(a);

                double planetX = r * Math.cos(trueAnomaly);
                double planetY = r * Math.sin(trueAnomaly);

                double dx = planetX - earthX;
                double dy = planetY - earthY;
                double d = Math.sqrt(dx*dx + dy*dy);

                double phi = Math.atan2(planetY, planetX) - Math.atan2(earthY, earthX);
                if (phi > Math.PI) phi -= 2 * Math.PI;
                if (phi < -Math.PI) phi += 2 * Math.PI;

                double Vx = v * Math.cos(phi) - v0;
                double Vy = v * Math.sin(phi);

                double sinB0 = (r * Math.sin(phi)) / d;
                double B = Math.asin(Math.min(1.0, Math.max(-1.0, sinB0)));

                if (r > r0) {
                    double condition = r * r - r0 * r0;
                    if (condition > 0 && d < Math.sqrt(condition)) {
                        if (B > 0) B = Math.PI - B;
                    }
                }

                double a_angle = Math.atan2(Vx, Vy);
                double U = a_angle + B - Math.PI / 2;
                double speed = Math.sqrt(Vx * Vx + Vy * Vy);
                double w = (speed * Math.abs(Math.sin(U))) / d;
                double direction = (U > 0) ? 1.0 : -1.0;

                double deltaLon = direction * w * 0.01;
                double lambda = Math.atan2(dy, dx);
                if (lambda < 0) lambda += 2 * Math.PI;
                lambda += deltaLon;
                if (lambda < 0) lambda += 2 * Math.PI;
                if (lambda >= 2 * Math.PI) lambda -= 2 * Math.PI;

                planet.setEclipticLon(lambda);
                planet.setDistanceAU(d);

                double epsilon = Math.toRadians(23.44);
                double beta = 0;
                double ra = Math.atan2(Math.sin(lambda) * Math.cos(epsilon) - Math.tan(beta) * Math.sin(epsilon),
                        Math.cos(lambda));
                double dec = Math.asin(Math.sin(beta) * Math.cos(epsilon) +
                        Math.cos(beta) * Math.sin(epsilon) * Math.sin(lambda));
                if (ra < 0) ra += 2 * Math.PI;

                planet.setRa(ra);
                planet.setDec(dec);
            }
        }
    }

    private double[] getSunScreenPosition(int cx, int cy) {
        double ra = -sunRa;
        double dec = sunDec;
        double x = Math.cos(dec) * Math.cos(ra);
        double y = Math.cos(dec) * Math.sin(ra);
        double z = Math.sin(dec);
        return projectToScreen(x, y, z, cx, cy);
    }

    private double[] getMoonScreenPosition(int cx, int cy) {
        double ra = -moonRa;
        double dec = moonDec;
        double x = Math.cos(dec) * Math.cos(ra);
        double y = Math.cos(dec) * Math.sin(ra);
        double z = Math.sin(dec);
        return projectToScreen(x, y, z, cx, cy);
    }

    private double[] getPlanetScreenPosition(PlanetData planet, int cx, int cy) {
        double ra = -planet.getRa();
        double dec = planet.getDec();
        double x = Math.cos(dec) * Math.cos(ra);
        double y = Math.cos(dec) * Math.sin(ra);
        double z = Math.sin(dec);
        return projectToScreen(x, y, z, cx, cy);
    }

    private void drawEcliptic(Graphics2D g2d, int cx, int cy) {
        double epsilon = Math.toRadians(23.44);
        g2d.setColor(new Color(255, 215, 0, 200));
        g2d.setStroke(new BasicStroke(1.5f));

        int prevX = -1, prevY = -1;
        for (int lambdaDeg = 0; lambdaDeg <= 360; lambdaDeg += 5) {
            double lambda = Math.toRadians(lambdaDeg);
            double ra = Math.atan2(Math.cos(epsilon) * Math.sin(lambda), Math.cos(lambda));
            double dec = -Math.asin(Math.sin(epsilon) * Math.sin(lambda));
            if (ra < 0) ra += 2 * Math.PI;

            double x = Math.cos(dec) * Math.cos(ra);
            double y = Math.cos(dec) * Math.sin(ra);
            double z = Math.sin(dec);

            double[] sp = projectToScreen(x, y, z, cx, cy);
            if (sp != null && sp[2] > 0) {
                if (prevX != -1 && Math.hypot(sp[0] - prevX, sp[1] - prevY) < 100) {
                    g2d.drawLine(prevX, prevY, (int) sp[0], (int) sp[1]);
                }
                prevX = (int) sp[0];
                prevY = (int) sp[1];
            } else {
                prevX = -1;
            }
        }
    }

    private double[] projectToScreen(double x, double y, double z, int cx, int cy) {
        double cosX = Math.cos(Math.toRadians(viewAngleX));
        double sinX = Math.sin(Math.toRadians(viewAngleX));
        double cosY = Math.cos(Math.toRadians(viewAngleY));
        double sinY = Math.sin(Math.toRadians(viewAngleY));

        double x1 = x * cosX - z * sinX;
        double z1 = x * sinX + z * cosX;
        double y1 = y;
        double x2 = x1;
        double y2 = y1 * cosY - z1 * sinY;
        double z2 = y1 * sinY + z1 * cosY;

        if (z2 <= 0) return null;

        double fov = 600 / Math.tan(Math.toRadians(fieldOfView) / 2);
        return new double[]{cx + (x2 * fov) / z2, cy - (y2 * fov) / z2, z2};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        updateSunPosition();
        updateMoonPosition();
        updatePlanetPositions();

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        starPositions.clear();
        messierPositions.clear();
        planetScreenPositions.clear();

        drawStars(g2d, cx, cy);
        if (showConstellations) drawConstellationLines(g2d);
        if (showBoundaries) drawBoundaries(g2d, cx, cy);
        if (showMessier) drawMessierObjects(g2d, cx, cy);
        if (showPlanets) drawPlanets(g2d, cx, cy);
        if (showEcliptic) drawEcliptic(g2d, cx, cy);
        if (showSun) drawSun(g2d, cx, cy);
        if (showMoon) drawMoon(g2d, cx, cy);
        if (showGrid) drawGrid(g2d, cx, cy);

        if (selectedSearchResult != null) {
            double ra = -selectedSearchResult.ra;
            double dec = selectedSearchResult.dec;
            double x = Math.cos(dec) * Math.cos(ra);
            double y = Math.cos(dec) * Math.sin(ra);
            double z = Math.sin(dec);
            double[] sp = projectToScreen(x, y, z, cx, cy);
            if (sp != null && sp[2] > 0) {
                g2d.setColor(new Color(255, 100, 100, 200));
                g2d.setStroke(new BasicStroke(3.0f));
                g2d.drawOval((int) sp[0] - 15, (int) sp[1] - 15, 30, 30);
                g2d.setColor(Color.YELLOW);
            }
        }

        drawInfoPanel(g2d);

        if (selectedObjectName != null) {
            g2d.setColor(new Color(0, 0, 0, 220));
            g2d.fillRect(cx - 200, getHeight() - 60, 400, 30);
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(selectedObjectName, cx - 180, getHeight() - 40);
        }
    }

    private void drawPlanets(Graphics2D g2d, int cx, int cy) {
        double zoomFactor = 60.0 / fieldOfView;

        for (PlanetData planet : planets) {
            double[] planetPos = getPlanetScreenPosition(planet, cx, cy);
            if (planetPos != null && planetPos[2] > 0) {
                planetScreenPositions.put(planet, planetPos);

                double angularSizeArcsec = planet.getAngularSizeArcsec();
                double size = Math.max(2, Math.min(12, angularSizeArcsec / 8.0 * Math.sqrt(zoomFactor)));

                g2d.setColor(planet.getColor());
                g2d.fillOval((int) planetPos[0] - (int) size / 2, (int) planetPos[1] - (int) size / 2, (int) size, (int) size);
                g2d.setColor(Color.WHITE);
                g2d.drawOval((int) planetPos[0] - (int) size / 2, (int) planetPos[1] - (int) size / 2, (int) size, (int) size);

                if (showLabels) {
                    g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                    String label = planet.isDwarf() ? "D " + planet.getName() : planet.getName();
                    g2d.drawString(label, (int) planetPos[0] + (int)size + 3, (int) planetPos[1] - 3);
                }
            }
        }
    }

    private void drawSun(Graphics2D g2d, int cx, int cy) {
        double[] sunPos = getSunScreenPosition(cx, cy);
        if (sunPos != null && sunPos[2] > 0) {
            double zoomFactor = 60.0 / fieldOfView;
            int size = (int)(14 * Math.sqrt(zoomFactor));
            size = Math.max(10, Math.min(24, size));

            RadialGradientPaint sunGlow = new RadialGradientPaint(
                    (float) sunPos[0], (float) sunPos[1], size,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(255, 255, 200, 200), new Color(255, 200, 100, 100), new Color(255, 200, 100, 0)}
            );
            g2d.setPaint(sunGlow);
            g2d.fillOval((int) sunPos[0] - size, (int) sunPos[1] - size, size * 2, size * 2);

            g2d.setColor(new Color(255, 220, 100));
            g2d.fillOval((int) sunPos[0] - size / 2, (int) sunPos[1] - size / 2, size, size);
        }
    }

    private void drawMoon(Graphics2D g2d, int cx, int cy) {
        double[] moonPos = getMoonScreenPosition(cx, cy);
        if (moonPos != null && moonPos[2] > 0) {
            double zoomFactor = 60.0 / fieldOfView;
            int size = (int)(10 * Math.sqrt(zoomFactor));
            size = Math.max(6, Math.min(16, size));

            g2d.setColor(new Color(220, 220, 220));
            g2d.fillOval((int) moonPos[0] - size / 2, (int) moonPos[1] - size / 2, size, size);

            if (moonIllumination < 0.99 && moonIllumination > 0.01) {
                int shadowWidth = (int)(size * (1 - moonIllumination));
                g2d.setColor(new Color(60, 60, 80));
                g2d.fillOval((int) moonPos[0] - size / 2, (int) moonPos[1] - size / 2, shadowWidth, size);
            }

            g2d.setColor(Color.WHITE);
            g2d.drawOval((int) moonPos[0] - size / 2, (int) moonPos[1] - size / 2, size, size);

            if (showLabels) {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString("Moon", (int) moonPos[0] + size + 3, (int) moonPos[1] - 3);
            }
        }
    }

    private void checkObjectSelection(int mouseX, int mouseY) {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = 15;

        if (showSun) {
            double[] sunPos = getSunScreenPosition(cx, cy);
            if (sunPos != null && sunPos[2] > 0) {
                double dx = mouseX - sunPos[0];
                double dy = mouseY - sunPos[1];
                if (Math.hypot(dx, dy) < radius) {
                    selectedObjectName = String.format("Sun | RA: %.2f h | Dec: %.2f°",
                            sunRa * 12 / Math.PI, Math.toDegrees(sunDec));
                    if (messageTimer.isRunning()) messageTimer.stop();
                    messageTimer.start();
                    repaint();
                    return;
                }
            }
        }

        if (showMoon) {
            double[] moonPos = getMoonScreenPosition(cx, cy);
            if (moonPos != null && moonPos[2] > 0) {
                double dx = mouseX - moonPos[0];
                double dy = mouseY - moonPos[1];
                if (Math.hypot(dx, dy) < radius) {
                    selectedObjectName = String.format("Moon | Phase: %.0f%% | RA: %.2f h | Dec: %.2f°",
                            moonIllumination * 100, moonRa * 12 / Math.PI, Math.toDegrees(moonDec));
                    if (messageTimer.isRunning()) messageTimer.stop();
                    messageTimer.start();
                    repaint();
                    return;
                }
            }
        }

        if (showPlanets) {
            for (Map.Entry<PlanetData, double[]> entry : planetScreenPositions.entrySet()) {
                double[] pos = entry.getValue();
                if (pos != null && pos[2] > 0) {
                    double dx = mouseX - pos[0];
                    double dy = mouseY - pos[1];
                    if (Math.hypot(dx, dy) < radius) {
                        PlanetData planet = entry.getKey();
                        selectedObjectName = String.format("%s | RA: %.2f h | Dec: %.2f° | Dist: %.2f AU",
                                planet.getName(), planet.getRa() * 12 / Math.PI,
                                Math.toDegrees(planet.getDec()), planet.getDistanceAU());
                        if (messageTimer.isRunning()) messageTimer.stop();
                        messageTimer.start();
                        repaint();
                        return;
                    }
                }
            }
        }

        for (Map.Entry<MessierObject, double[]> entry : messierPositions.entrySet()) {
            double[] pos = entry.getValue();
            if (pos != null) {
                double dx = mouseX - pos[0];
                double dy = mouseY - pos[1];
                if (Math.hypot(dx, dy) < 8) {
                    MessierObject obj = entry.getKey();
                    selectedObjectName = String.format("M%d: %s (%s, %.1fm)",
                            obj.getNumber(), obj.getName(), obj.getType(), obj.getMagnitude());
                    if (messageTimer.isRunning()) messageTimer.stop();
                    messageTimer.start();
                    repaint();
                    return;
                }
            }
        }
    }

    private void drawStars(Graphics2D g2d, int cx, int cy) {
        if (stars == null) return;

        double magnitudeLimit = 6.5 + (60.0 - fieldOfView) / 15.0;
        magnitudeLimit = Math.min(8.5, Math.max(4.5, magnitudeLimit));

        for (HygStar star : stars) {
            if (star.getMag() > magnitudeLimit) continue;

            double ra = -star.getRa();
            double dec = star.getDec();

            double x = Math.cos(dec) * Math.cos(ra);
            double y = Math.cos(dec) * Math.sin(ra);
            double z = Math.sin(dec);
            double[] sp = projectToScreen(x, y, z, cx, cy);

            if (sp != null && sp[2] > 0) {
                starPositions.put(star, sp);
                double mag = star.getMag();
                double size = Math.max(1, 5 - mag * 0.3);
                int alpha = (int) Math.max(80, Math.min(255, 255 - (int) (mag * 15)));
                Color col = star.getColor();
                g2d.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha));
                g2d.fillOval((int) sp[0] - (int) size / 2, (int) sp[1] - (int) size / 2, (int) size, (int) size);

                if (showLabels && mag < 4.0 && star.getName() != null && !star.getName().isEmpty()) {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                    g2d.drawString(star.getName(), (int) sp[0] + 5, (int) sp[1] - 3);
                }
            }
        }
    }

    private void drawConstellationLines(Graphics2D g2d) {
        if (constellationLines == null) return;
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setColor(new Color(100, 200, 255, 150));

        for (int[] line : constellationLines) {
            if (line[0] >= stars.size() || line[1] >= stars.size()) continue;
            double[] p1 = starPositions.get(stars.get(line[0]));
            double[] p2 = starPositions.get(stars.get(line[1]));
            if (p1 != null && p2 != null && Math.hypot(p1[0] - p2[0], p1[1] - p2[1]) < 400) {
                g2d.drawLine((int) p1[0], (int) p1[1], (int) p2[0], (int) p2[1]);
            }
        }
    }

    private void drawBoundaries(Graphics2D g2d, int cx, int cy) {
        if (boundaryPoints == null || boundaryPoints.isEmpty()) return;
        g2d.setStroke(new BasicStroke(0.8f));
        g2d.setColor(new Color(80, 180, 80, 100));

        Map<String, List<double[]>> constellationBoundaryMap = new HashMap<>();
        for (BoundaryPoint bp : boundaryPoints) {
            String key = bp.getConstellation1();
            if (key == null) continue;

            double ra = -bp.getRa();
            double dec = bp.getDec();

            double x = Math.cos(dec) * Math.cos(ra);
            double y = Math.cos(dec) * Math.sin(ra);
            double z = Math.sin(dec);

            constellationBoundaryMap.computeIfAbsent(key, k -> new ArrayList<>()).add(new double[]{x, y, z});
        }

        for (List<double[]> points : constellationBoundaryMap.values()) {
            for (int i = 0; i < points.size() - 1; i++) {
                double[] sp1 = projectToScreen(points.get(i)[0], points.get(i)[1], points.get(i)[2], cx, cy);
                double[] sp2 = projectToScreen(points.get(i + 1)[0], points.get(i + 1)[1], points.get(i + 1)[2], cx, cy);
                if (sp1 != null && sp2 != null && Math.hypot(sp1[0] - sp2[0], sp1[1] - sp2[1]) < 300) {
                    g2d.drawLine((int) sp1[0], (int) sp1[1], (int) sp2[0], (int) sp2[1]);
                }
            }
        }
    }

    private void drawMessierObjects(Graphics2D g2d, int cx, int cy) {
        if (messierObjects == null) return;

        for (MessierObject obj : messierObjects) {
            double ra = -obj.getRa();
            double dec = obj.getDec();

            double x = Math.cos(dec) * Math.cos(ra);
            double y = Math.cos(dec) * Math.sin(ra);
            double z = Math.sin(dec);
            double[] sp = projectToScreen(x, y, z, cx, cy);

            if (sp != null && sp[2] > 0) {
                messierPositions.put(obj, sp);
                g2d.setColor(obj.getColor());
                g2d.fillOval((int) sp[0] - 3, (int) sp[1] - 3, 6, 6);
                g2d.setColor(Color.WHITE);
                g2d.drawOval((int) sp[0] - 3, (int) sp[1] - 3, 6, 6);
                if (showLabels) {
                    g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                    g2d.drawString("M" + obj.getNumber(), (int) sp[0] + 5, (int) sp[1] - 3);
                }
            }
        }
    }

    private void drawGrid(Graphics2D g2d, int cx, int cy) {
        g2d.setStroke(new BasicStroke(0.8f));
        g2d.setColor(new Color(100, 150, 255, 100));

        for (int meridian = 0; meridian < 360; meridian += 30) {
            double raRad = Math.toRadians(meridian);
            int prevX = -1, prevY = -1;
            boolean first = true;
            for (int dec = -85; dec <= 85; dec += 3) {
                double decRad = Math.toRadians(dec);
                double x = Math.cos(decRad) * Math.cos(raRad);
                double y = Math.cos(decRad) * Math.sin(raRad);
                double z = Math.sin(decRad);
                double[] sp = projectToScreen(x, y, z, cx, cy);
                if (sp != null && sp[2] > 0) {
                    if (!first) {
                        g2d.drawLine(prevX, prevY, (int) sp[0], (int) sp[1]);
                    }
                    prevX = (int) sp[0];
                    prevY = (int) sp[1];
                    first = false;
                } else {
                    first = true;
                }
            }
        }

        g2d.setColor(new Color(255, 200, 0, 150));
        double[][] equatorPoints = new double[361][];
        for (int ra = 0; ra <= 360; ra++) {
            double raRad = Math.toRadians(ra);
            double x = Math.cos(raRad);
            double y = Math.sin(raRad);
            double z = 0;
            double[] sp = projectToScreen(x, y, z, cx, cy);
            if (sp != null) equatorPoints[ra] = sp;
        }
        for (int ra = 0; ra <= 359; ra++) {
            if (equatorPoints[ra] != null && equatorPoints[ra + 1] != null) {
                g2d.drawLine((int) equatorPoints[ra][0], (int) equatorPoints[ra][1],
                        (int) equatorPoints[ra + 1][0], (int) equatorPoints[ra + 1][1]);
            }
        }

        g2d.setColor(new Color(100, 150, 255, 60));
        for (int decDeg = -75; decDeg <= 75; decDeg += 15) {
            if (decDeg == 0) continue;
            double decRad = Math.toRadians(decDeg);
            int prevX = -1, prevY = -1;
            for (int raDeg = 0; raDeg <= 360; raDeg += 5) {
                double ra = Math.toRadians(raDeg);
                double x = Math.cos(decRad) * Math.cos(ra);
                double y = Math.cos(decRad) * Math.sin(ra);
                double z = Math.sin(decRad);
                double[] sp = projectToScreen(x, y, z, cx, cy);
                if (sp != null && sp[2] > 0) {
                    if (prevX != -1 && Math.hypot(sp[0] - prevX, sp[1] - prevY) < 200) {
                        g2d.drawLine(prevX, prevY, (int) sp[0], (int) sp[1]);
                    }
                    prevX = (int) sp[0];
                    prevY = (int) sp[1];
                } else {
                    prevX = -1;
                }
            }
        }

        g2d.setColor(new Color(150, 200, 255, 200));
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));

        for (int raHour = 0; raHour < 24; raHour++) {
            double raRad = Math.toRadians(raHour * 15);
            double dec = 0;
            double[] sp = projectToScreen(Math.cos(dec) * Math.cos(raRad),
                    Math.cos(dec) * Math.sin(raRad),
                    Math.sin(dec), cx, cy);
            if (sp != null && sp[2] > 0) {
                int displayHour = (24 - raHour) % 24;
                g2d.drawString(displayHour + "h", (int) sp[0] + 3, (int) sp[1] + 5);
            }
        }

        for (int decDeg = -75; decDeg <= 75; decDeg += 15) {
            if (decDeg == 0) continue;
            double decRad = Math.toRadians(decDeg);
            double ra = 0;
            double[] sp = projectToScreen(Math.cos(decRad) * Math.cos(ra),
                    Math.cos(decRad) * Math.sin(ra),
                    Math.sin(decRad), cx, cy);
            if (sp != null && sp[2] > 0) {
                String sign = decDeg > 0 ? "+" : "";
                g2d.drawString(sign + decDeg + "°", (int) sp[0] - 25, (int) sp[1] + 3);
            }
        }
    }

    private void drawInfoPanel(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(10, 10, 450, 110);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

        double magnitudeLimit = 6.5 + (60.0 - fieldOfView) / 15.0;
        magnitudeLimit = Math.min(8.5, Math.max(4.5, magnitudeLimit));

        double cosX = Math.cos(Math.toRadians(viewAngleX));
        double sinX = Math.sin(Math.toRadians(viewAngleX));
        double cosY = Math.cos(Math.toRadians(viewAngleY));
        double sinY = Math.sin(Math.toRadians(viewAngleY));

        double cx = 0, cy = 0, cz = 1;

        double tempY = cy * cosY + cz * sinY;
        double tempZ = -cy * sinY + cz * cosY;
        cy = tempY;
        cz = tempZ;

        double tempX = cx * cosX + cz * sinX;
        tempZ = -cx * sinX + cz * cosX;
        cx = tempX;
        cz = tempZ;

        double dec = Math.asin(Math.max(-1, Math.min(1, cz)));
        double ra = 2*Math.PI-Math.atan2(cy, cx);
        if (ra > 2*Math.PI) ra = -Math.atan2(cy, cx);

        int y = 30;
        g2d.drawString(String.format("Moon: %.0f%%", moonIllumination * 100), 20, y);
        y += 20;
        g2d.drawString(String.format("Sun: RA=%.2f h | Dec=%.2f°", sunRa * 12 / Math.PI, Math.toDegrees(sunDec)), 20, y);
        y += 20;
        g2d.drawString(String.format("Zoom: %.0f° | Limit: %.1fm", fieldOfView, magnitudeLimit), 20, y);
        y += 20;
        g2d.drawString(String.format("Center: RA=%.2f h | Dec=%.1f°",
                ra * 12 / Math.PI, Math.toDegrees(dec)), 20, y);
    }


    public void setGridVisible(boolean v) { showGrid = v; repaint(); }
    public void setConstellationsVisible(boolean v) { showConstellations = v; repaint(); }
    public void setBoundariesVisible(boolean v) { showBoundaries = v; repaint(); }
    public void setLabelsVisible(boolean v) { showLabels = v; repaint(); }
    public void setMessierVisible(boolean v) { showMessier = v; repaint(); }
    public void setPlanetsVisible(boolean v) { showPlanets = v; repaint(); }
    public void setSunVisible(boolean v) { showSun = v; repaint(); }
    public void setMoonVisible(boolean v) { showMoon = v; repaint(); }
    public void setEclipticVisible(boolean v) { showEcliptic = v; repaint(); }
    public void resetView() { viewAngleX = 0; viewAngleY = 90; fieldOfView = 60; repaint(); }

    public boolean isGridVisible() { return showGrid; }
    public boolean isConstellationsVisible() { return showConstellations; }
    public boolean isBoundariesVisible() { return showBoundaries; }
    public boolean isLabelsVisible() { return showLabels; }
    public boolean isMessierVisible() { return showMessier; }
    public boolean isPlanetsVisible() { return showPlanets; }
    public boolean isSunVisible() { return showSun; }
    public boolean isMoonVisible() { return showMoon; }
    public boolean isEclipticVisible() { return showEcliptic; }
}