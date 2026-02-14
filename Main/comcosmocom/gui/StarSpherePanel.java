package comcosmocom.gui;

import comcosmocom.data.JsonStarLoader;
import comcosmocom.model.Star;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class StarSpherePanel extends JPanel {
    private List<Star> stars;
    private List<int[]> constellationLines = new ArrayList<>();
    private Map<Star, double[]> starPositions = new HashMap<>();
    private double viewAngleX = 0, viewAngleY = 0, fieldOfView = 60.0;
    private int lastMouseX, lastMouseY;
    private boolean showGrid = true, showConstellations = true, showLabels = true;

    public StarSpherePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1200, 900));

        JsonStarLoader loader = new JsonStarLoader();
        try {
            loader.loadJsonStars("resources/data/stars.8.json");
            stars = loader.getStars();
            loadConstellationLines("resources/data/constellationship.fab", loader.getHipToStarMap());
        } catch (IOException e) {
            e.printStackTrace();
            stars = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Ошибка загрузки данных: " + e.getMessage());
        }

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;
                viewAngleX += dx * 0.3;
                viewAngleY += dy * 0.3;
                if (viewAngleY > 90) viewAngleY = 90;
                if (viewAngleY < -90) viewAngleY = -90;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                repaint();
            }
        });

        addMouseWheelListener(e -> {
            fieldOfView += e.getWheelRotation() * 2;
            fieldOfView = Math.max(20, Math.min(120, fieldOfView));
            repaint();
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: viewAngleX -= 5; break;
                    case KeyEvent.VK_RIGHT: viewAngleX += 5; break;
                    case KeyEvent.VK_UP: viewAngleY -= 5; break;
                    case KeyEvent.VK_DOWN: viewAngleY += 5; break;
                    case KeyEvent.VK_SPACE: viewAngleX = 0; viewAngleY = 0; fieldOfView = 60; break;
                    case KeyEvent.VK_G: showGrid = !showGrid; break;
                    case KeyEvent.VK_C: showConstellations = !showConstellations; break;
                    case KeyEvent.VK_L: showLabels = !showLabels; break;
                }
                repaint();
            }
            return false;
        });
        setFocusable(true);
    }

    private void loadConstellationLines(String filename, Map<Integer, Star> hipToStarMap) {
        constellationLines.clear();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            for (String line : lines) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.trim().split("\\s+");
                if (parts.length < 3) continue;

                List<Integer> hipIds = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    try {
                        hipIds.add(Integer.parseInt(parts[i]));
                    } catch (NumberFormatException ignored) {}
                }
                for (int j = 0; j < hipIds.size() - 1; j++) {
                    Star star1 = hipToStarMap.get(hipIds.get(j));
                    Star star2 = hipToStarMap.get(hipIds.get(j + 1));
                    if (star1 != null && star2 != null) {
                        constellationLines.add(new int[]{star1.getId(), star2.getId()});
                    }
                }
            }
            System.out.println("Загружено линий созвездий: " + constellationLines.size());
        } catch (IOException e) {
            System.err.println("Не удалось загрузить созвездия: " + e.getMessage());
        }
    }

    private double[] projectToScreen(double x, double y, double z, int cx, int cy, double scale) {
        double cosX = Math.cos(Math.toRadians(viewAngleX)), sinX = Math.sin(Math.toRadians(viewAngleX));
        double cosY = Math.cos(Math.toRadians(viewAngleY)), sinY = Math.sin(Math.toRadians(viewAngleY));
        double x1 = x * cosX - z * sinX, z1 = x * sinX + z * cosX, y1 = y;
        double x2 = x1, y2 = y1 * cosY - z1 * sinY, z2 = y1 * sinY + z1 * cosY;
        if (z2 <= 0) return null;
        double dist = scale / Math.tan(Math.toRadians(fieldOfView) / 2);
        return new double[]{cx + (x2 * dist) / z2, cy - (y2 * dist) / z2, z2};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2, cy = getHeight() / 2;
        double scale = Math.min(getWidth(), getHeight()) * 0.8;

        starPositions.clear();

        for (Star star : stars) {
            double ra = star.getRightAscension(), dec = star.getDeclination();
            double x = Math.cos(dec) * Math.cos(ra), y = Math.cos(dec) * Math.sin(ra), z = Math.sin(dec);
            double[] sp = projectToScreen(x, y, z, cx, cy, scale);
            if (sp != null) {
                starPositions.put(star, sp);
                double size = Math.max(2, 10 - star.getMagnitude() * 0.8);
                g2d.setColor(star.getColor());
                g2d.fillOval((int)sp[0] - (int)size/2, (int)sp[1] - (int)size/2, (int)size, (int)size);
                if (showLabels) {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 11));
                    g2d.drawString(String.format("HIP%d", star.getId()), (int)sp[0] + 5, (int)sp[1] - 5);
                }
            }
        }

        if (showConstellations) {
            g2d.setColor(new Color(100, 200, 255, 150));
            g2d.setStroke(new BasicStroke(1.2f));
            for (int[] line : constellationLines) {
                if (line.length >= 2 && line[0] < stars.size() && line[1] < stars.size()) {
                    Star s1 = stars.get(line[0]);
                    Star s2 = stars.get(line[1]);
                    double[] p1 = starPositions.get(s1);
                    double[] p2 = starPositions.get(s2);
                    if (p1 != null && p2 != null) {
                        g2d.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
                    }
                }
            }
        }

        drawInfo(g2d);
    }

    private void drawInfo(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(10, 10, 400, 80);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        int y = 30;
        g2d.drawString(String.format("Звёзд: %d  Сетка: %s  Созвездия: %s  Подписи: %s",
                stars.size(),
                showGrid ? "Вкл" : "Выкл",
                showConstellations ? "Вкл" : "Выкл",
                showLabels ? "Вкл" : "Выкл"), 20, y);
        y += 20;
        g2d.drawString("G-сетка, C-созвездия, L-подписи, Пробел-сброс", 20, y);
    }

    // === ВСЕ НЕОБХОДИМЫЕ ПУБЛИЧНЫЕ МЕТОДЫ ===
    public void setGridVisible(boolean visible) {
        showGrid = visible;
        repaint();
    }
    public void setConstellationsVisible(boolean visible) {
        showConstellations = visible;
        repaint();
    }

    public void setLabelsVisible(boolean visible) {
        showLabels = visible;
        repaint();
    }

    public void resetView() {
        viewAngleX = 0;
        viewAngleY = 0;
        fieldOfView = 60;
        repaint();
    }

    public boolean isGridVisible() {
        return showGrid;
    }

    public boolean isConstellationsVisible() {
        return showConstellations;
    }

    public boolean isLabelsVisible() {
        return showLabels;
    }

    public int getStarCount() {
        return stars != null ? stars.size() : 0;
    }
}

