package src.comcosmocom.gui;

import src.comcosmocom.data.HygLoader;
import src.comcosmocom.model.HygStar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class StarSpherePanel extends JPanel {
    private List<HygStar> stars;
    private List<int[]> constellationLines = new ArrayList<>();
    private Map<HygStar, double[]> starPositions = new HashMap<>();
    private double viewAngleX = 0, viewAngleY = 0, fieldOfView = 60.0;
    private int lastMouseX, lastMouseY;
    private boolean showGrid = true, showConstellations = true, showLabels = true;

    public StarSpherePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1200, 900));

        try {
            HygLoader loader = new HygLoader();
            loader.loadHygData("hygdata_v3.csv");
            stars = loader.getStars();
            loadConstellationLines("constellationship.fab", loader.getHipMap());

            System.out.println("Готово! Звёзд: " + stars.size() + ", линий: " + constellationLines.size());

        } catch (Exception e) {
            e.printStackTrace();
            stars = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }

        // Управление мышью
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

                // Инвертированное управление: мышь вправо - небо влево
                viewAngleX -= dx * 0.3;
                viewAngleY -= dy * 0.3;

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

    private void loadConstellationLines(String filename, Map<Integer, HygStar> hipMap) {
        constellationLines.clear();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            int lineCount = 0;
            int connectionCount = 0;

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

                // Соединяем последовательные звёзды
                for (int i = 0; i < hipIds.size() - 1; i++) {
                    int hip1 = hipIds.get(i);
                    int hip2 = hipIds.get(i + 1);

                    HygStar star1 = hipMap.get(hip1);
                    HygStar star2 = hipMap.get(hip2);

                    if (star1 != null && star2 != null) {
                        constellationLines.add(new int[]{star1.getId(), star2.getId()});
                        connectionCount++;
                    }
                }
                lineCount++;
            }

            System.out.println("Созвездий в файле: " + lineCount);
            System.out.println("Реальных соединений: " + connectionCount);

        } catch (Exception e) {
            System.out.println("Ошибка загрузки созвездий: " + e.getMessage());
        }
    }

    private double[] projectToScreen(double x, double y, double z, int cx, int cy, double scale) {
        double cosX = Math.cos(Math.toRadians(viewAngleX));
        double sinX = Math.sin(Math.toRadians(viewAngleX));
        double cosY = Math.cos(Math.toRadians(viewAngleY));
        double sinY = Math.sin(Math.toRadians(viewAngleY));

        // Поворот камеры
        double x1 = x * cosX - z * sinX;
        double z1 = x * sinX + z * cosX;
        double y1 = y;

        double x2 = x1;
        double y2 = y1 * cosY - z1 * sinY;
        double z2 = y1 * sinY + z1 * cosY;

        if (z2 <= 0) return null;

        // Стереографическая проекция (эффект "рыбьего глаза")
        double dist = scale * 1.5;

        double theta = Math.acos(z2 / Math.sqrt(x2*x2 + y2*y2 + z2*z2));
        double r = 2 * dist * Math.tan(theta / 2);

        double screenX = cx + (x2 / Math.sqrt(x2*x2 + y2*y2)) * r;
        double screenY = cy - (y2 / Math.sqrt(x2*x2 + y2*y2)) * r;

        return new double[]{screenX, screenY, z2};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        double scale = Math.min(getWidth(), getHeight()) * 0.8;

        starPositions.clear();

        // Рисуем звёзды с учётом яркости
        for (HygStar star : stars) {
            double x = Math.cos(star.getDec()) * Math.cos(star.getRa());
            double y = Math.cos(star.getDec()) * Math.sin(star.getRa());
            double z = Math.sin(star.getDec());

            double[] sp = projectToScreen(x, y, z, cx, cy, scale);

            if (sp != null && sp[2] > 0) {
                starPositions.put(star, sp);

                double mag = star.getMag();

                // Размер в зависимости от яркости
                double size;
                if (mag < 1.0) size = 6.0;
                else if (mag < 2.0) size = 4.0;
                else if (mag < 3.0) size = 3.0;
                else if (mag < 4.0) size = 2.0;
                else if (mag < 5.0) size = 1.5;
                else size = 1.0;

                // Прозрачность в зависимости от яркости
                int alpha;
                if (mag < 1.0) alpha = 255;
                else if (mag < 2.0) alpha = 220;
                else if (mag < 3.0) alpha = 180;
                else if (mag < 4.0) alpha = 140;
                else if (mag < 5.0) alpha = 100;
                else alpha = 60;

                Color starColor = star.getColor();
                g2d.setColor(new Color(
                        starColor.getRed(),
                        starColor.getGreen(),
                        starColor.getBlue(),
                        alpha
                ));

                g2d.fillOval((int)sp[0] - (int)size/2, (int)sp[1] - (int)size/2,
                        (int)size, (int)size);

                // Названия только для ярких звёзд
                if (showLabels && mag < 2.0 && !star.getName().isEmpty()) {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 11));
                    g2d.drawString(star.getName(), (int)sp[0] + 5, (int)sp[1] - 5);
                }
            }
        }

        // Рисуем линии созвездий
        if (showConstellations) {
            g2d.setStroke(new BasicStroke(1.2f));

            for (int[] line : constellationLines) {
                HygStar s1 = stars.get(line[0]);
                HygStar s2 = stars.get(line[1]);

                double[] p1 = starPositions.get(s1);
                double[] p2 = starPositions.get(s2);

                if (p1 != null && p2 != null && p1[2] > 0 && p2[2] > 0) {
                    float alpha = 0.3f;
                    if (s1.getMag() < 3.0 && s2.getMag() < 3.0) {
                        alpha = 0.8f;
                    }
                    g2d.setColor(new Color(100, 200, 255, (int)(alpha * 255)));
                    g2d.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
                }
            }
        }

        // Информация
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(10, 10, 500, 100);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

        int y = 30;
        g2d.drawString(String.format("Звёзд: %d  Линий: %d  Сетка: %s  Созвездия: %s",
                stars.size(), constellationLines.size(),
                showGrid ? "Вкл" : "Выкл",
                showConstellations ? "Вкл" : "Выкл"), 20, y);
        y += 20;
        g2d.drawString("G-сетка, C-созвездия, L-подписи, Пробел-сброс", 20, y);
        y += 20;
        g2d.drawString("Управление: мышь - захват и поворот неба", 20, y);
    }

    public void setGridVisible(boolean v) { showGrid = v; repaint(); }
    public void setConstellationsVisible(boolean v) { showConstellations = v; repaint(); }
    public void setLabelsVisible(boolean v) { showLabels = v; repaint(); }
    public void resetView() { viewAngleX = 0; viewAngleY = 0; fieldOfView = 60; repaint(); }
    public boolean isGridVisible() { return showGrid; }
    public boolean isConstellationsVisible() { return showConstellations; }
    public boolean isLabelsVisible() { return showLabels; }
}
