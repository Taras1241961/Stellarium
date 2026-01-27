package comcosmocom.gui;

import comcosmocom.data.JsonStarLoader;
import comcosmocom.model.Star;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class StarSpherePanel extends JPanel {
    private List<Star> stars = new ArrayList<>();
    private double rotationX = 0;
    private double rotationY = 0;
    private double zoom = 1.0;
    private int lastMouseX, lastMouseY;
    private boolean showGrid = true;

    // Конструктор
    public StarSpherePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1000, 800));

        loadStars();

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
                rotationY += dx * 0.01;
                rotationX += dy * 0.01;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                repaint();
            }
        });

        addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            if (notches > 0) {
                zoom *= 0.9;
            } else {
                zoom *= 1.1;
            }
            if (zoom < 0.1) zoom = 0.1;
            if (zoom > 5.0) zoom = 5.0;
            repaint();
        });
    }

    private void loadStars() {
        stars.clear();

        // Создаём 5000 случайных звёзд (как в реальном небе)
        int totalStars = 5000; // можно увеличить до 10000

        for (int i = 0; i < totalStars; i++) {
            // Случайное прямое восхождение (0-24 часа)
            double raHours = Math.random() * 24.0;
            // Случайное склонение (-90 до +90 градусов)
            double decDeg = (Math.random() * 180.0) - 90.0;
            // Случайная звёздная величина (0-8, где 0 — самая яркая)
            double mag = Math.random() * 8.0;

            // Преобразуем в радианы
            double raRad = raHours * (Math.PI / 12.0);
            double decRad = decDeg * (Math.PI / 180.0);

            // Случайный B-V индекс для цвета
            double bvIndex = (Math.random() * 2.0) - 0.5; // от -0.5 до +1.5

            Star star = new Star(i, raRad, decRad, mag, bvIndex);
            stars.add(star);
        }

        System.out.println("Created " + stars.size() + " random stars");

        // Добавляем несколько реальных ярких звёзд для ориентира
        addRealBrightStars();
    }

    private void addRealBrightStars() {
        // 20 самых ярких реальных звёзд
        double[][] brightStars = {
                // RA(часы), Dec(градусы), Magnitude, Название
                {6.7525, -16.716, -1.46},   // Сириус
                {5.2423, -8.2016, -0.72},   // Канопус
                {5.9195, 7.4071, 0.42},     // Бетельгейзе
                {19.8464, 8.8922, 0.76},    // Альтаир
                {18.6171, 38.7836, 0.03},   // Вега
                {10.1396, 11.9672, 0.34},   // Процион
                {4.5987, 16.5093, 0.85},    // Альдебаран
                {14.6608, -60.8352, 0.61},  // Хадар
                {12.6954, -48.9596, -0.27}, // Альфа Центавра
                {16.4901, -26.432, 0.96},   // Антарес
                {6.4381, -52.6956, 0.18},   // Канопус (южный)
                {7.6550, 5.225, 1.14},      // Ригель
                {5.2782, 45.998, 1.90},     // Капелла
                {17.5826, -37.103, 2.29},   // Фомальгаут
                {2.5303, 89.264, 1.97},     // Полярная звезда
                {9.4595, -54.708, 1.50},    // Ахернар
                {13.4199, -11.161, 0.98},   // Спика
                {1.6286, -57.237, 2.82},    // Ахернар (другой)
                {20.6905, 45.280, 2.23},    // Денеб
                {22.9608, -29.622, 1.16}    // Фомальгаут (южный)
        };

        int startId = stars.size();
        for (int i = 0; i < brightStars.length; i++) {
            double raHours = brightStars[i][0];
            double decDeg = brightStars[i][1];
            double mag = brightStars[i][2];

            double raRad = raHours * (Math.PI / 12.0);
            double decRad = decDeg * (Math.PI / 180.0);

            // Яркие звёзды обычно бело-жёлтые
            double bvIndex = 0.3 + Math.random() * 0.4;

            Star star = new Star(startId + i, raRad, decRad, mag, bvIndex);
            stars.add(star);
        }

        System.out.println("Added " + brightStars.length + " real bright stars");
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double sphereRadius = Math.min(centerX, centerY) * 0.8 * zoom;

        if (showGrid) {
            drawGrid(g2d, centerX, centerY, sphereRadius);
        }

        for (Star star : stars) {
            drawStar(g2d, star, centerX, centerY, sphereRadius);
        }

        drawInfo(g2d);
    }

    private void drawStar(Graphics2D g2d, Star star,
                          int centerX, int centerY, double radius) {

        // Получаем 3D координаты звезды на сфере
        double[] coords3D = star.getCartesianCoordinates(radius);
        double x = coords3D[0];
        double y = coords3D[1];
        double z = coords3D[2];

        // Вращение ВОКРУГ наблюдателя (мы внутри сферы!)
        // Вместо вращения сферы, вращаем координаты наблюдателя
        double xRotated = x * Math.cos(rotationY) - z * Math.sin(rotationY);
        double zRotated = x * Math.sin(rotationY) + z * Math.cos(rotationY);
        double yRotated = y * Math.cos(rotationX) - zRotated * Math.sin(rotationX);
        double zFinal = y * Math.sin(rotationX) + zRotated * Math.cos(rotationX);

        // Перспективная проекция (как будто мы внутри)
        // Близкие звёзды должны быть больше, дальние — меньше

        // Расстояние от наблюдателя (мы в центре)
        double distance = Math.sqrt(xRotated*xRotated + yRotated*yRotated + zFinal*zFinal);

        // Если звезда ПЕРЕД нами (z > 0) — рисуем, если позади — нет
        if (zFinal <= 0) return; // звезда позади нас

        // Коэффициент перспективы (близкие звёзды больше)
        double perspective = radius / (distance + radius * 0.5);

        // Экранные координаты
        double screenX = centerX + (xRotated / distance) * radius * zoom;
        double screenY = centerY - (yRotated / distance) * radius * zoom;

        // Размер звезды с учётом перспективы и яркости
        double baseSize = 6.0;
        double size = (baseSize - star.getMagnitude()) * perspective;
        size = Math.max(0.5, size); // минимальный размер

        // Яркость с учётом расстояния
        float brightness = (float) (1.0 / (1.0 + distance/(radius*2)));

        Color color = star.getColor();
        // Делаем цвет темнее для дальних звёзд
        int red = (int)(color.getRed() * brightness);
        int green = (int)(color.getGreen() * brightness);
        int blue = (int)(color.getBlue() * brightness);
        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));

        Color adjustedColor = new Color(red, green, blue);

        // Рисуем звезду
        g2d.setColor(adjustedColor);
        int intSize = (int) Math.max(1, size);
        g2d.fillOval((int)screenX - intSize/2, (int)screenY - intSize/2,
                intSize, intSize);

        // Для очень ярких звёзд добавляем свечение
        if (star.getMagnitude() < 1.0 && intSize > 3) {
            g2d.setColor(new Color(red, green, blue, 80));
            int glowSize = intSize * 2;
            g2d.fillOval((int)screenX - glowSize/2, (int)screenY - glowSize/2,
                    glowSize, glowSize);
        }
    }


    private void drawGrid(Graphics2D g2d, int centerX, int centerY, double radius) {
        g2d.setColor(new Color(50, 50, 100, 150));
        g2d.setStroke(new BasicStroke(1));

        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3;

            int prevX = 0, prevY = 0;
            boolean first = true;

            for (int lat = -90; lat <= 90; lat += 10) {
                double latRad = Math.toRadians(lat);
                double x = radius * Math.cos(latRad) * Math.cos(angle);
                double y = radius * Math.cos(latRad) * Math.sin(angle);
                double z = radius * Math.sin(latRad);

                double xRot = x * Math.cos(rotationY) - z * Math.sin(rotationY);
                double zRot = x * Math.sin(rotationY) + z * Math.cos(rotationY);
                double yRot = y * Math.cos(rotationX) - zRot * Math.sin(rotationX);
                double zFinal = y * Math.sin(rotationX) + zRot * Math.cos(rotationX);

                if (zFinal < 0) {
                    first = true;
                    continue;
                }

                int screenX = (int) (centerX + xRot);
                int screenY = (int) (centerY - yRot);

                if (!first) {
                    g2d.drawLine(prevX, prevY, screenX, screenY);
                }

                prevX = screenX;
                prevY = screenY;
                first = false;
            }
        }
    }

    private void drawInfo(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("CosmoComputer | Stars: " + stars.size() + " | Zoom: " + String.format("%.1f", zoom), 10, 20);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Controls: drag to rotate, mouse wheel to zoom", 10, getHeight() - 30);
        g2d.drawString("Inspired by 'George's Secret Key to the Universe'", 10, getHeight() - 10);
    }

    // === ВСЕ НЕОБХОДИМЫЕ МЕТОДЫ ===
    public double getRotationX() { return rotationX; }
    public double getRotationY() { return rotationY; }
    public double getZoom() { return zoom; }
    public int getStarCount() { return stars.size(); }
    public boolean isGridVisible() { return showGrid; }

    public void setRotationX(double rotationX) {
        this.rotationX = rotationX;
        repaint();
    }

    public void setRotationY(double rotationY) {
        this.rotationY = rotationY;
        repaint();
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        if (this.zoom < 0.1) this.zoom = 0.1;
        if (this.zoom > 5.0) this.zoom = 5.0;
        repaint();
    }

    public void setGridVisible(boolean visible) {
        this.showGrid = visible;
        repaint();
    }

    public void toggleGrid() {
        showGrid = !showGrid;
        repaint();
        System.out.println("Grid: " + (showGrid ? "ON" : "OFF"));
    }

    public void toggleConstellations() {
        System.out.println("Constellations feature coming soon");
    }

    public void resetView() {
        rotationX = 0;
        rotationY = 0;
        zoom = 1.0;
        repaint();
        System.out.println("View reset");
    }

    public void rotateLeft() {
        rotationY -= 0.2;
        repaint();
    }

    public void rotateRight() {
        rotationY += 0.2;
        repaint();
    }

    public void rotateUp() {
        rotationX -= 0.2;
        repaint();
    }

    public void rotateDown() {
        rotationX += 0.2;
        repaint();
    }

    public void zoomIn() {
        setZoom(zoom * 1.2);
    }

    public void zoomOut() {
        setZoom(zoom * 0.8);
    }
}
