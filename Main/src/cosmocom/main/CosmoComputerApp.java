package src.cosmocom.main;

import src.cosmocom.gui.StarSpherePanel;
import src.cosmocom.utils.TimeUtils;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CosmoComputerApp {
    private static StarSpherePanel starPanel;
    private static JLabel dateLabel;
    private static JLabel timeLabel;
    private static JLabel jdLabel;
    private static JLabel timeOffsetLabel;
    private static JLabel timeSpeedLabel;
    private static JLabel timeStatusLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CosmoComputer - 3D Star Atlas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1500, 950);
            frame.setLocationRelativeTo(null);

            starPanel = new StarSpherePanel();
            frame.add(starPanel, BorderLayout.CENTER);

            JPanel rightPanel = createRightPanel();
            frame.add(rightPanel, BorderLayout.EAST);

            JPanel controlPanel = createControlPanel();
            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.setJMenuBar(createMenuBar());
            frame.setVisible(true);

            Timer timer = new Timer(100, e -> {
                updateTimeDisplay();
                updateTimeControlDisplay();
                starPanel.repaint();
            });
            timer.start();
        });
    }

    private static JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0, 200));
        panel.setPreferredSize(new Dimension(260, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 2));

        JLabel titleLabel = new JLabel("=== MOSCOW TIME (MSK) ===");
        titleLabel.setForeground(new Color(100, 200, 255));
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(15));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));

        JPanel datePanel = new JPanel();
        datePanel.setBackground(new Color(50, 50, 70));
        datePanel.setMaximumSize(new Dimension(220, 30));
        datePanel.setLayout(new FlowLayout());
        dateLabel = new JLabel("YYYY-MM-DD");
        dateLabel.setForeground(Color.CYAN);
        dateLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        datePanel.add(dateLabel);
        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(10));

        JPanel timePanel = new JPanel();
        timePanel.setBackground(new Color(50, 50, 70));
        timePanel.setMaximumSize(new Dimension(220, 35));
        timePanel.setLayout(new FlowLayout());
        timeLabel = new JLabel("HH:MM:SS");
        timeLabel.setForeground(Color.YELLOW);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        timePanel.add(timeLabel);
        panel.add(timePanel);
        panel.add(Box.createVerticalStrut(15));

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(100, 150, 200));
        sep1.setMaximumSize(new Dimension(220, 3));
        panel.add(sep1);
        panel.add(Box.createVerticalStrut(10));

        JLabel jdTitle = new JLabel("Julian Date");
        jdTitle.setForeground(new Color(150, 150, 150));
        jdTitle.setFont(new Font("Monospaced", Font.PLAIN, 10));
        jdTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(jdTitle);

        JPanel jdPanel = new JPanel();
        jdPanel.setBackground(new Color(50, 50, 70));
        jdPanel.setMaximumSize(new Dimension(220, 25));
        jdPanel.setLayout(new FlowLayout());
        jdLabel = new JLabel("2450000.00000");
        jdLabel.setForeground(Color.GREEN);
        jdLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        jdPanel.add(jdLabel);
        panel.add(jdPanel);
        panel.add(Box.createVerticalStrut(15));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(100, 150, 200));
        sep2.setMaximumSize(new Dimension(220, 3));
        panel.add(sep2);
        panel.add(Box.createVerticalStrut(10));

        JLabel timeControlTitle = new JLabel("TIME CONTROL");
        timeControlTitle.setForeground(new Color(255, 200, 100));
        timeControlTitle.setFont(new Font("Monospaced", Font.BOLD, 11));
        timeControlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(timeControlTitle);
        panel.add(Box.createVerticalStrut(10));

        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(50, 50, 70));
        statusPanel.setMaximumSize(new Dimension(220, 25));
        statusPanel.setLayout(new FlowLayout());
        timeStatusLabel = new JLabel("Status: PAUSED");
        timeStatusLabel.setForeground(Color.RED);
        timeStatusLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        statusPanel.add(timeStatusLabel);
        panel.add(statusPanel);
        panel.add(Box.createVerticalStrut(5));

        JPanel offsetPanel = new JPanel();
        offsetPanel.setBackground(new Color(50, 50, 70));
        offsetPanel.setMaximumSize(new Dimension(220, 25));
        offsetPanel.setLayout(new FlowLayout());
        timeOffsetLabel = new JLabel("Offset: 0.0 days");
        timeOffsetLabel.setForeground(Color.CYAN);
        timeOffsetLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        offsetPanel.add(timeOffsetLabel);
        panel.add(offsetPanel);
        panel.add(Box.createVerticalStrut(5));

        JPanel speedPanel = new JPanel();
        speedPanel.setBackground(new Color(50, 50, 70));
        speedPanel.setMaximumSize(new Dimension(220, 25));
        speedPanel.setLayout(new FlowLayout());
        timeSpeedLabel = new JLabel("Speed: 1.0x");
        timeSpeedLabel.setForeground(Color.YELLOW);
        timeSpeedLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        speedPanel.add(timeSpeedLabel);
        panel.add(speedPanel);
        panel.add(Box.createVerticalStrut(10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 5, 5));
        buttonPanel.setBackground(new Color(30, 30, 50));
        buttonPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 100), 1));
        buttonPanel.setMaximumSize(new Dimension(120, 200));

        JButton rewindFastBtn = new JButton("xBack");
        rewindFastBtn.setFont(new Font("Monospaced", Font.BOLD, 11));
        rewindFastBtn.setBackground(new Color(60, 60, 80));
        rewindFastBtn.setForeground(Color.WHITE);
        rewindFastBtn.addActionListener(e -> starPanel.timeRewindFast());
        buttonPanel.add(rewindFastBtn);

        JButton rewindBtn = new JButton("Back");
        rewindBtn.setFont(new Font("Monospaced", Font.BOLD, 11));
        rewindBtn.setBackground(new Color(60, 60, 80));
        rewindBtn.setForeground(Color.WHITE);
        rewindBtn.addActionListener(e -> starPanel.timeRewind());
        buttonPanel.add(rewindBtn);

        JButton playPauseBtn = new JButton("Pause");
        playPauseBtn.setFont(new Font("Monospaced", Font.BOLD, 11));
        playPauseBtn.setBackground(new Color(0, 100, 0));
        playPauseBtn.setForeground(Color.WHITE);
        playPauseBtn.addActionListener(e -> {
            starPanel.timePlayPause();
            playPauseBtn.setText(starPanel.isTimePlaying() ? "Start" : "Pause");
            playPauseBtn.setBackground(starPanel.isTimePlaying() ? new Color(0, 150, 0) : new Color(0, 100, 0));
        });
        buttonPanel.add(playPauseBtn);

        JButton forwardBtn = new JButton("Go");
        forwardBtn.setFont(new Font("Monospaced", Font.BOLD, 11));
        forwardBtn.setBackground(new Color(60, 60, 80));
        forwardBtn.setForeground(Color.WHITE);
        forwardBtn.addActionListener(e -> starPanel.timeForward());
        buttonPanel.add(forwardBtn);

        JButton forwardFastBtn = new JButton("xGo");
        forwardFastBtn.setFont(new Font("Monospaced", Font.BOLD, 11));
        forwardFastBtn.setBackground(new Color(60, 60, 80));
        forwardFastBtn.setForeground(Color.WHITE);
        forwardFastBtn.addActionListener(e -> starPanel.timeForwardFast());
        buttonPanel.add(forwardFastBtn);

        JButton resetTimeBtn = new JButton("Repeat");
        resetTimeBtn.setFont(new Font("Monospaced", Font.BOLD, 11));
        resetTimeBtn.setBackground(new Color(100, 50, 0));
        resetTimeBtn.setForeground(Color.WHITE);
        resetTimeBtn.addActionListener(e -> starPanel.timeReset());
        buttonPanel.add(resetTimeBtn);

        panel.add(buttonPanel);
        panel.add(Box.createVerticalStrut(15));

        return panel;
    }

    private static void updateTimeDisplay() {
        dateLabel.setText(TimeUtils.getCurrentDateString());
        timeLabel.setText(TimeUtils.getCurrentTimeString());
        jdLabel.setText(TimeUtils.getCurrentJDString());
    }

    private static void updateTimeControlDisplay() {
        if (starPanel != null) {
            timeOffsetLabel.setText(String.format("Offset: %+.1f days", starPanel.getTimeOffsetDays()));
            timeSpeedLabel.setText(String.format("Speed: %.1fx", starPanel.getTimeSpeed()));
            timeStatusLabel.setText(starPanel.isTimePlaying() ? "Status: PLAYING" : "Status: PAUSED");
            timeStatusLabel.setForeground(starPanel.isTimePlaying() ? Color.GREEN : Color.RED);
        }
    }

    private static JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 50));
        panel.setLayout(new FlowLayout());

        JButton resetViewBtn = new JButton("Reset View");
        resetViewBtn.setBackground(new Color(0, 100, 150));
        resetViewBtn.setForeground(Color.WHITE);
        resetViewBtn.addActionListener(e -> starPanel.resetView());
        panel.add(resetViewBtn);

        JButton gridBtn = new JButton("Grid: ON");
        gridBtn.setBackground(new Color(50, 50, 80));
        gridBtn.setForeground(Color.WHITE);
        gridBtn.addActionListener(e -> {
            starPanel.setGridVisible(!starPanel.isGridVisible());
            gridBtn.setText(starPanel.isGridVisible() ? "Grid: ON" : "Grid: OFF");
        });
        panel.add(gridBtn);

        JButton constBtn = new JButton("Const: ON");
        constBtn.setBackground(new Color(50, 50, 80));
        constBtn.setForeground(Color.WHITE);
        constBtn.addActionListener(e -> {
            starPanel.setConstellationsVisible(!starPanel.isConstellationsVisible());
            constBtn.setText(starPanel.isConstellationsVisible() ? "Const: ON" : "Const: OFF");
        });
        panel.add(constBtn);

        JButton boundariesBtn = new JButton("Boundaries: ON");
        boundariesBtn.setBackground(new Color(50, 50, 80));
        boundariesBtn.setForeground(Color.WHITE);
        boundariesBtn.addActionListener(e -> {
            starPanel.setBoundariesVisible(!starPanel.isBoundariesVisible());
            boundariesBtn.setText(starPanel.isBoundariesVisible() ? "Boundaries: ON" : "Boundaries: OFF");
        });
        panel.add(boundariesBtn);

        JButton labelsBtn = new JButton("Labels: ON");
        labelsBtn.setBackground(new Color(50, 50, 80));
        labelsBtn.setForeground(Color.WHITE);
        labelsBtn.addActionListener(e -> {
            starPanel.setLabelsVisible(!starPanel.isLabelsVisible());
            labelsBtn.setText(starPanel.isLabelsVisible() ? "Labels: ON" : "Labels: OFF");
        });
        panel.add(labelsBtn);

        JButton messierBtn = new JButton("Messier: ON");
        messierBtn.setBackground(new Color(50, 50, 80));
        messierBtn.setForeground(Color.WHITE);
        messierBtn.addActionListener(e -> {
            starPanel.setMessierVisible(!starPanel.isMessierVisible());
            messierBtn.setText(starPanel.isMessierVisible() ? "Messier: ON" : "Messier: OFF");
        });
        panel.add(messierBtn);

        JButton planetsBtn = new JButton("Planets: ON");
        planetsBtn.setBackground(new Color(50, 50, 80));
        planetsBtn.setForeground(Color.WHITE);
        planetsBtn.addActionListener(e -> {
            starPanel.setPlanetsVisible(!starPanel.isPlanetsVisible());
            planetsBtn.setText(starPanel.isPlanetsVisible() ? "Planets: ON" : "Planets: OFF");
        });
        panel.add(planetsBtn);

        JButton sunBtn = new JButton("Sun: ON");
        sunBtn.setBackground(new Color(50, 50, 80));
        sunBtn.setForeground(Color.WHITE);
        sunBtn.addActionListener(e -> {
            starPanel.setSunVisible(!starPanel.isSunVisible());
            sunBtn.setText(starPanel.isSunVisible() ? "Sun: ON" : "Sun: OFF");
        });
        panel.add(sunBtn);

        JButton moonBtn = new JButton("Moon: ON");
        moonBtn.setBackground(new Color(50, 50, 80));
        moonBtn.setForeground(Color.WHITE);
        moonBtn.addActionListener(e -> {
            starPanel.setMoonVisible(!starPanel.isMoonVisible());
            moonBtn.setText(starPanel.isMoonVisible() ? "Moon: ON" : "Moon: OFF");
        });
        panel.add(moonBtn);

        JButton eclipticBtn = new JButton("Ecliptic: ON");
        eclipticBtn.setBackground(new Color(50, 50, 80));
        eclipticBtn.setForeground(Color.WHITE);
        eclipticBtn.addActionListener(e -> {
            starPanel.setEclipticVisible(!starPanel.isEclipticVisible());
            eclipticBtn.setText(starPanel.isEclipticVisible() ? "Ecliptic: ON" : "Ecliptic: OFF");
        });
        panel.add(eclipticBtn);

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(30, 30, 50));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.ORANGE), "SEARCH",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 10), Color.ORANGE));

        JTextField searchField = new JTextField(15);
        searchField.setBackground(new Color(50, 50, 70));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.addActionListener(e -> starPanel.search(searchField.getText()));

        JButton searchBtn = new JButton("Find");
        searchBtn.setBackground(new Color(0, 100, 100));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> starPanel.search(searchField.getText()));

        JButton prevSearchBtn = new JButton("Prev");
        prevSearchBtn.setBackground(new Color(50, 50, 80));
        prevSearchBtn.setForeground(Color.WHITE);
        prevSearchBtn.addActionListener(e -> starPanel.previousSearchResult());

        JButton nextSearchBtn = new JButton("Next");
        nextSearchBtn.setBackground(new Color(50, 50, 80));
        nextSearchBtn.setForeground(Color.WHITE);
        nextSearchBtn.addActionListener(e -> starPanel.nextSearchResult());

        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(prevSearchBtn);
        searchPanel.add(nextSearchBtn);

        panel.add(searchPanel);

        return panel;
    }

    private static JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu viewMenu = new JMenu("View");

        JCheckBoxMenuItem gridItem = new JCheckBoxMenuItem("Grid", true);
        gridItem.addActionListener(e -> starPanel.setGridVisible(gridItem.isSelected()));

        JCheckBoxMenuItem constItem = new JCheckBoxMenuItem("Constellations", true);
        constItem.addActionListener(e -> starPanel.setConstellationsVisible(constItem.isSelected()));

        JCheckBoxMenuItem boundItem = new JCheckBoxMenuItem("Boundaries", true);
        boundItem.addActionListener(e -> starPanel.setBoundariesVisible(boundItem.isSelected()));

        JCheckBoxMenuItem labelItem = new JCheckBoxMenuItem("Labels", true);
        labelItem.addActionListener(e -> starPanel.setLabelsVisible(labelItem.isSelected()));

        JCheckBoxMenuItem messierItem = new JCheckBoxMenuItem("Messier", true);
        messierItem.addActionListener(e -> starPanel.setMessierVisible(messierItem.isSelected()));

        viewMenu.add(gridItem);
        viewMenu.add(constItem);
        viewMenu.add(boundItem);
        viewMenu.add(labelItem);
        viewMenu.add(messierItem);
        bar.add(viewMenu);

        return bar;
    }
}