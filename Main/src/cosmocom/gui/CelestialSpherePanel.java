package src.cosmocom.gui;

import src.cosmocom.utils.TimeUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CelestialSpherePanel extends JPanel implements MouseListener, MouseMotionListener {

    private final double latitude = 43.3499;
    private final double longitude = 42.4453;
    private final String locationName = "Mount Elbrus";

    private double headAzimuth = 0.0;
    private double headAltitude = 0.0;

    private int lastMouseX, lastMouseY;

    public CelestialSpherePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1200, 900));
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2 - 50;

        // Чёрный фон
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // === 1. СФЕРА ===
        g2d.setColor(new Color(80, 100, 150, 100));
        g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // === 2. ЛИНИЯ ГОРИЗОНТА ===
        int horizonWidth = radius;
        int horizonHeight = (int)(radius * Math.cos(Math.toRadians(headAltitude)));
        int horizonCenterY = centerY + (int)(radius * 0.3 * Math.sin(Math.toRadians(headAltitude)));

        g2d.setColor(new Color(255, 200, 100, 220));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawOval(centerX - horizonWidth, horizonCenterY - horizonHeight,
                horizonWidth * 2, horizonHeight * 2);

        // === 3. ТОЧКИ СТОРОН СВЕТА НА ГОРИЗОНТЕ ===
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 16));

        // Север (верхняя точка)
        int nX = centerX;
        int nY = horizonCenterY - horizonHeight;
        if (nY > 0) {
            g2d.drawString("N", nX - 5, nY - 8);
        }

        // Юг (нижняя точка)
        int sX = centerX;
        int sY = horizonCenterY + horizonHeight;
        if (sY < getHeight()) {
            g2d.drawString("S", sX - 5, sY + 15);
        }

        // Восток (правая точка)
        int eX = centerX + horizonWidth;
        int eY = horizonCenterY;
        if (eX < getWidth()) {
            g2d.drawString("E", eX + 5, eY + 5);
        }

        // Запад (левая точка)
        int wX = centerX - horizonWidth;
        int wY = horizonCenterY;
        if (wX > 0) {
            g2d.drawString("W", wX - 15, wY + 5);
        }

        // === 4. ТОЧКА ЗЕНИТА ===
        g2d.setColor(Color.RED);
        g2d.fillOval(centerX - 5, centerY - 5, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2d.drawString("ZENITH", centerX + 8, centerY - 8);

        // === 5. ИНФОРМАЦИЯ ===
        drawInfoPanel(g2d);
    }

    private void drawInfoPanel(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(10, 10, 450, 100);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

        String dateTime = TimeUtils.getCurrentDateTimeString();
        int y = 30;
        g2d.drawString("SURFACE MODE - " + locationName, 20, y);
        y += 20;
        g2d.drawString(String.format("Lat: %.2f° | Lon: %.2f°", latitude, longitude), 20, y);
        y += 20;
        g2d.drawString(String.format("UTC: %s | Head: Az=%.0f° Alt=%.0f°", dateTime, headAzimuth, headAltitude), 20, y);
        y += 20;
        g2d.drawString("Controls: Drag mouse to rotate | SPACE to exit", 20, y);
    }

    public void resetView() { headAzimuth = 0; headAltitude = 0; repaint(); }

    @Override
    public void mousePressed(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - lastMouseX;
        int dy = e.getY() - lastMouseY;
        headAzimuth += dx * 0.5;
        headAltitude += dy * 0.5;
        if (headAltitude > 89) headAltitude = 89;
        if (headAltitude < -89) headAltitude = -89;
        if (headAzimuth < 0) headAzimuth += 360;
        if (headAzimuth >= 360) headAzimuth -= 360;
        lastMouseX = e.getX();
        lastMouseY = e.getY();
        repaint();
    }

    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
}