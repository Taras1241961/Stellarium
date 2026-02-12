package comcosmocom.gui;

import comcosmocom.model.Star;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StarSpherePanel extends JPanel {
    private List<Star> stars;
    private Map<Star, double[]> starPositions;

    private double viewAngleX = 0;      // Азимут (вращение вокруг вертикали)
    private double viewAngleY = 0;      // Высота над горизонтом
    private double fieldOfView = 60.0;  // Поле зрения в градусах

    private int lastMouseX, lastMouseY;
    private boolean showGrid = true;
    private boolean showConstellations = true;
    private boolean showLabels = true;

    // Данные для созвездий: пары индексов звёзд
    private List<int[]> constellationLines;

    public StarSpherePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1200, 900));

        starPositions = new HashMap<>();
        constellationLines = new ArrayList<>();

        loadStars();
        loadConstellations();

        // Управление мышью
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;

                viewAngleX += dx * 0.3;
                viewAngleY += dy * 0.3;

                // Ограничиваем вертикальный угол, чтобы не уйти за горизонт
                if (viewAngleY > 90) viewAngleY = 90;
                if (viewAngleY < -90) viewAngleY = -90;

                lastMouseX = e.getX();
                lastMouseY = e.getY();
                repaint();
            }
        });

        addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            fieldOfView += notches * 2;

            if (fieldOfView < 20) fieldOfView = 20;
            if (fieldOfView > 120) fieldOfView = 120;

            repaint();
        });

        // Клавиши управления
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_PRESSED) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_LEFT:
                                viewAngleX -= 5;
                                break;
                            case KeyEvent.VK_RIGHT:
                                viewAngleX += 5;
                                break;
                            case KeyEvent.VK_UP:
                                viewAngleY -= 5;
                                break;
                            case KeyEvent.VK_DOWN:
                                viewAngleY += 5;
                                break;
                            case KeyEvent.VK_SPACE:
                                viewAngleX = 0;
                                viewAngleY = 0;
                                fieldOfView = 60;
                                break;
                            case KeyEvent.VK_G:
                                showGrid = !showGrid;
                                break;
                            case KeyEvent.VK_C:
                                showConstellations = !showConstellations;
                                break;
                            case KeyEvent.VK_L:
                                showLabels = !showLabels;
                                break;
                        }
                        repaint();
                    }
                    return false;
                });

        setFocusable(true);
    }

    private void loadStars() {
        stars = new ArrayList<>();

        // Ярчайшие звёзды северного и южного полушарий
        double[][] starData = {
                // RA (ч), Dec (°), mag, B-V, название
                {6.75, -16.72, -1.46, 0.0, "Сириус"},
                {5.24, -8.20, -0.72, 0.0, "Канопус"},
                {14.66, -60.84, -0.01, 0.0, "Толиман"},
                {14.39, 19.18, 0.03, 0.0, "Арктур"},
                {18.62, 38.78, 0.03, 0.0, "Вега"},
                {5.92, 7.41, 0.42, 0.0, "Капелла"},
                {6.45, -52.70, 0.50, 0.0, "Ригель"},
                {7.63, 5.23, 0.60, 0.0, "Процион"},
                {4.60, 16.51, 0.85, 0.0, "Альдебаран"},
                {19.85, 8.89, 0.76, 0.0, "Альтаир"},
                {5.29, -8.95, 1.37, 0.0, "Бетельгейзе"},
                {12.70, -48.96, -0.27, 0.0, "Альфа Центавра"},
                {16.49, -26.43, 0.96, 0.0, "Антарес"},
                {17.56, 12.56, 2.10, 0.0, "Рас Альхаг"},
                {3.29, 15.18, 2.01, 0.0, "Алголь"},
                {1.63, 12.49, 2.27, 0.0, "Альферац"},
                {22.08, -0.32, 3.40, 0.0, "Эниф"},
                {20.78, 45.28, 2.45, 0.0, "Денеб"},
                {21.98, 12.71, 2.90, 0.0, "Пегас"},
                {0.20, 27.26, 3.10, 0.0, "Андромеда"}
        };

        for (int i = 0; i < starData.length; i++) {
            double raHours = starData[i][0];
            double decDeg = starData[i][1];
            double mag = starData[i][2];
            double bv = starData[i][3];
            String name = (String) starData[i][4];

            double raRad = raHours * 15 * Math.PI / 180;
            double decRad = decDeg * Math.PI / 180;

            Star star = new Star(i, name, raRad, decRad, mag, bv);
            stars.add(star);
        }

        System.out.println("Загружено звёзд: " + stars.size());
    }

    private void loadConstellations() {
        // Созвездие Орион
        constellationLines.add(new int[]{2, 4});   // Бетельгейзе - Ригель
        constellationLines.add(new int[]{2, 5});   // Бетельгейзе - Беллатрикс
        constellationLines.add(new int[]{4, 5});   // Ригель - Беллатрикс
        constellationLines.add(new int[]{2, 6});   // Бетельгейзе - Минтака
        constellationLines.add(new int[]{4, 6});   // Ригель - Минтака

        // Большая Медведица
        constellationLines.add(new int[]{7, 8});
        constellationLines.add(new int[]{8, 9});
        constellationLines.add(new int[]{9, 10});
        constellationLines.add(new int[]{10, 11});

        // Лебедь
        constellationLines.add(new int[]{17, 18});
        constellationLines.add(new int[]{18, 19});
        constellationLines.add(new int[]{19, 20});
    }

    private double[] projectToScreen(double x, double y, double z,
                                     int centerX, int centerY, double scale) {
        // Поворот камеры (вид изнутри сферы)
        double cosX = Math.cos(Math.toRadians(viewAngleX));
        double sinX = Math.sin(Math.toRadians(viewAngleX));
        double cosY = Math.cos(Math.toRadians(viewAngleY));
        double sinY = Math.sin(Math.toRadians(viewAngleY));

        // Поворот вокруг оси Y (горизонтальный)
        double x1 = x * cosX - z * sinX;
        double z1 = x * sinX + z * cosX;
        double y1 = y;

        // Поворот вокруг оси X (вертикальный)
        double x2 = x1;
        double y2 = y1 * cosY - z1 * sinY;
        double z2 = y1 * sinY + z1 * cosY;

        // Перспективная проекция (вид из центра сферы наружу)
        if (z2 <= 0) return null;

        double fovRad = Math.toRadians(fieldOfView);
        double distance = scale / Math.tan(fovRad / 2);

        double screenX = centerX + (x2 * distance) / z2;
        double screenY = centerY - (y2 * distance) / z2;

        return new double[]{screenX, screenY, z2};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double scale = Math.min(getWidth(), getHeight()) * 0.8;

        // Звёздный фон
        drawStars(g2d, centerX, centerY, scale);

        // Сетка координат
        if (showGrid) {
            drawCoordinateGrid(g2d, centerX, centerY, scale);
        }

        // Сохраняем позиции звёзд для созвездий
        starPositions.clear();

        // Рисуем звёзды
        for (Star star : stars) {
            double ra = star.getRightAscension();
            double dec = star.getDeclination();

            // Сферические координаты -> декартовы на сфере радиуса 1
            double x = Math.cos(dec) * Math.cos(ra);
            double y = Math.cos(dec) * Math.sin(ra);
            double z = Math.sin(dec);

            double[] screenPos = projectToScreen(x, y, z, centerX, centerY, scale);

            if (screenPos != null) {
                starPositions.put(star, screenPos);

                double magnitude = star.getMagnitude();
                double size = 12 - magnitude * 0.5;
                if (size < 2) size = 2;
                if (size > 10) size = 10;

                g2d.setColor(star.getColor());
                g2d.fillOval((int)screenPos[0] - (int)size/2,
                        (int)screenPos[1] - (int)size/2,
                        (int)size, (int)size);

                // Подпись звезды
                if (showLabels && star.getName() != null) {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 11));

                    int labelX = (int)screenPos[0] + (int)size + 3;
                    int labelY = (int)screenPos[1] - 5;

                    g2d.drawString(star.getName(), labelX, labelY);

                    // Звёздная величина
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.setFont(new Font("Arial", Font.PLAIN, 9));
                    g2d.drawString(String.format("m=%.1f", magnitude),
                            labelX, labelY + 12);
                }
            }
        }

        // Рисуем созвездия
        if (showConstellations) {
            g2d.setColor(new Color(100, 200, 255, 150));
            g2d.setStroke(new BasicStroke(1.2f));

            for (int[] line : constellationLines) {
                if (line.length >= 2) {
                    Star star1 = stars.get(line[0]);
                    Star star2 = stars.get(line[1]);

                    double[] pos1 = starPositions.get(star1);
                    double[] pos2 = starPositions.get(star2);

                    if (pos1 != null && pos2 != null) {
                        g2d.drawLine((int)pos1[0], (int)pos1[1],
                                (int)pos2[0], (int)pos2[1]);
                    }
                }
            }
        }

        // Информационная панель
        drawInfoPanel(g2d);
    }

    private void drawStars(Graphics2D g2d, int cx, int cy, double scale) {
// Фоновые звёзды (имитация Млечного Пути)
        g2d.setColor(new Color(255, 255, 200, 80));
        for (int i = 0; i < 1000; i++) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            int size = (int)(Math.random() * 2) + 1;
            g2d.fillOval(x, y, size, size);
        }
    }

    private void drawCoordinateGrid(Graphics2D g2d, int cx, int cy, double scale) {
        g2d.setColor(new Color(0, 100, 255, 100));
        g2d.setStroke(new BasicStroke(1));

        // Меридианы (RA)
        for (int ra = 0; ra < 360; ra += 30) {
            double raRad = Math.toRadians(ra);

            int prevX = 0, prevY = 0;
            boolean first = true;

            for (int dec = -80; dec <= 80; dec += 10) {
                double decRad = Math.toRadians(dec);

                double x = Math.cos(decRad) * Math.cos(raRad);
                double y = Math.cos(decRad) * Math.sin(raRad);
                double z = Math.sin(decRad);

                double[] screenPos = projectToScreen(x, y, z, cx, cy, scale);

                if (screenPos != null) {
                    if (!first) {
                        g2d.drawLine(prevX, prevY, (int)screenPos[0], (int)screenPos[1]);
                    }
                    prevX = (int)screenPos[0];
                    prevY = (int)screenPos[1];
                    first = false;
                } else {
                    first = true;
                }
            }
        }

        // Параллели (Dec)
        for (int dec = -80; dec <= 80; dec += 20) {
            if (dec == 0) {
                g2d.setColor(new Color(255, 200, 0, 150)); // Экватор жёлтый
                g2d.setStroke(new BasicStroke(1.5f));
            } else {
                g2d.setColor(new Color(0, 150, 255, 80));
                g2d.setStroke(new BasicStroke(1));
            }

            double decRad = Math.toRadians(dec);

            int prevX = 0, prevY = 0;
            boolean first = true;

            for (int ra = 0; ra <= 360; ra += 10) {
                double raRad = Math.toRadians(ra);

                double x = Math.cos(decRad) * Math.cos(raRad);
                double y = Math.cos(decRad) * Math.sin(raRad);
                double z = Math.sin(decRad);

                double[] screenPos = projectToScreen(x, y, z, cx, cy, scale);

                if (screenPos != null) {
                    if (!first) {
                        g2d.drawLine(prevX, prevY, (int)screenPos[0], (int)screenPos[1]);
                    }
                    prevX = (int)screenPos[0];
                    prevY = (int)screenPos[1];
                    first = false;
                } else {
                    first = true;
                }
            }
        }
    }

    private void drawInfoPanel(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(10, 10, 380, 100);

        g2d.setColor(new Color(200, 220, 255));
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

        int y = 30;
        g2d.drawString("КОСМОКОМПЬЮТЕР - Исследователь Вселенной", 20, y); y += 20;
        g2d.drawString(String.format("Азимут: %.1f°  Высота: %.1f°  Поле зрения: %.1f°",
                viewAngleX, viewAngleY, fieldOfView), 20, y); y += 20;
        g2d.drawString(String.format("Звёзд: %d  Сетка: %s  Созвездия: %s  Подписи: %s",
                stars.size(),
                showGrid ? "Вкл" : "Выкл",
                showConstellations ? "Вкл" : "Выкл",
                showLabels ? "Вкл" : "Выкл"), 20, y); y += 20;
        g2d.drawString("Управление: мышь - поворот, колесо - зум, G-сетка, C-созвездия, L-подписи, Пробел-сброс", 20, y);
    }

    // Публичные методы для управления
    public void setGridVisible(boolean visible) { showGrid = visible; repaint(); }
    public void setConstellationsVisible(boolean visible) { showConstellations = visible; repaint(); }
    public void setLabelsVisible(boolean visible) { showLabels = visible; repaint(); }
    public void resetView() { viewAngleX = 0; viewAngleY = 0; fieldOfView = 60; repaint(); }

    public double getViewAngleX() { return viewAngleX; }
    public double getViewAngleY() { return viewAngleY; }
    public double getFieldOfView() { return fieldOfView; }
    public int getStarCount() { return stars.size(); }
    public boolean isGridVisible() { return showGrid; }
    public boolean isConstellationsVisible() { return showConstellations; }
    public boolean isLabelsVisible() { return showLabels; }
}
