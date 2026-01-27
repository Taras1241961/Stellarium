package comcosmocom.main;

import comcosmocom.gui.StarSpherePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CosmoComputerApp {

    public static void main(String[] args) {
        // Запускаем в потоке обработки событий Swing
        SwingUtilities.invokeLater(CosmoComputerApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Создаём главное окно
        JFrame frame = new JFrame("КосмоКомпьютер - Исследователь Вселенной");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setLocationRelativeTo(null); // центрируем окно

        // Создаём панель со звёздной сферой
        StarSpherePanel spherePanel = new StarSpherePanel();
        frame.add(spherePanel, BorderLayout.CENTER);

        // Создаём панель управления
        JPanel controlPanel = createControlPanel(spherePanel);
        frame.add(controlPanel, BorderLayout.SOUTH);

        // Создаём меню
        JMenuBar menuBar = createMenuBar(spherePanel);
        frame.setJMenuBar(menuBar);

        // Делаем окно видимым
        frame.setVisible(true);

        System.out.println("=== КосмоКомпьютер запущен! ===");
        System.out.println("Звёзд загружено: " + spherePanel.getStarCount());
        System.out.println("Управление: перетаскивание - вращение, колесико - масштаб");
    }

    private static JPanel createControlPanel(StarSpherePanel spherePanel) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 40, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Кнопка сброса
        JButton resetButton = new JButton("Сбросить вид");
        resetButton.addActionListener(e -> spherePanel.resetView());

        // 2. Кнопка переключения сетки
        JButton gridButton = new JButton("Сетка координат");
        gridButton.addActionListener(e -> spherePanel.toggleGrid());

        // 3. Панель управления масштабом
        JLabel zoomLabel = new JLabel("Масштаб:");
        zoomLabel.setForeground(Color.WHITE);

        JButton zoomInButton = new JButton("+");
        zoomInButton.addActionListener(e -> {
            double currentZoom = spherePanel.getZoom();
            spherePanel.setZoom(currentZoom * 1.2);
        });

        JButton zoomOutButton = new JButton("-");
        zoomOutButton.addActionListener(e -> {
            double currentZoom = spherePanel.getZoom();
            spherePanel.setZoom(currentZoom * 0.8);
        });

        JButton zoomResetButton = new JButton("100%");
        zoomResetButton.addActionListener(e -> spherePanel.setZoom(1.0));

        // 4. Панель управления вращением
        JLabel rotateLabel = new JLabel("Вращение:");
        rotateLabel.setForeground(Color.WHITE);

        JButton rotateLeftButton = new JButton("←");
        rotateLeftButton.addActionListener(e -> {
            double currentRotation = spherePanel.getRotationY();
            spherePanel.setRotationY(currentRotation - 0.5);
        });

        JButton rotateRightButton = new JButton("→");
        rotateRightButton.addActionListener(e -> {
            double currentRotation = spherePanel.getRotationY();
            spherePanel.setRotationY(currentRotation + 0.5);
        });

        JButton rotateUpButton = new JButton("↑");
        rotateUpButton.addActionListener(e -> {
            double currentRotation = spherePanel.getRotationX();
            spherePanel.setRotationX(currentRotation - 0.5);
        });

        JButton rotateDownButton = new JButton("↓");
        rotateDownButton.addActionListener(e -> {
            double currentRotation = spherePanel.getRotationX();
            spherePanel.setRotationX(currentRotation + 0.5);
        });

        // 5. Информационная панель
        JLabel infoLabel = new JLabel("Звёзд: " + spherePanel.getStarCount());
        infoLabel.setForeground(Color.YELLOW);

        // Добавляем все компоненты на панель
        panel.add(resetButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(gridButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));

        panel.add(zoomLabel);
        panel.add(zoomOutButton);
        panel.add(zoomResetButton);
        panel.add(zoomInButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));

        panel.add(rotateLabel);
        panel.add(rotateLeftButton);
        panel.add(rotateRightButton);
        panel.add(rotateUpButton);
        panel.add(rotateDownButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));

        panel.add(infoLabel);

        return panel;
    }

    private static JMenuBar createMenuBar(StarSpherePanel spherePanel) {
        JMenuBar menuBar = new JMenuBar();

        // === Меню "Файл" ===
        JMenu fileMenu = new JMenu("Файл");

        JMenuItem saveViewItem = new JMenuItem("Сохранить вид...");
        saveViewItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                    "Функция сохранения вида будет реализована позже",
                    "Информация", JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveViewItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // === Меню "Вид" ===
        JMenu viewMenu = new JMenu("Вид");

        JCheckBoxMenuItem gridMenuItem = new JCheckBoxMenuItem("Сетка координат", true);
        gridMenuItem.addActionListener(e -> {
            boolean selected = gridMenuItem.isSelected();
            spherePanel.setGridVisible(selected);
        });

        JMenuItem constellationsItem = new JMenuItem("Созвездия (скоро)");
        constellationsItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                    "Отображение созвездий будет добавлено в следующей версии",
                    "В разработке", JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem zoomInItem = new JMenuItem("Приблизить");
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke('=', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        zoomInItem.addActionListener(e -> {
            double currentZoom = spherePanel.getZoom();
            spherePanel.setZoom(currentZoom * 1.2);
        });

        JMenuItem zoomOutItem = new JMenuItem("Отдалить");
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke('-', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        zoomOutItem.addActionListener(e -> {
            double currentZoom = spherePanel.getZoom();
            spherePanel.setZoom(currentZoom * 0.8);
        });

        JMenuItem resetViewItem = new JMenuItem("Сбросить вид");
        resetViewItem.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        resetViewItem.addActionListener(e -> spherePanel.resetView());

        viewMenu.add(gridMenuItem);
        viewMenu.add(constellationsItem);
        viewMenu.addSeparator();
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(resetViewItem);

        // === Меню "Справка" ===
        JMenu helpMenu = new JMenu("Справка");

        JMenuItem controlsItem = new JMenuItem("Управление");
        controlsItem.addActionListener(e -> showControlsDialog());

        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(controlsItem);
        helpMenu.add(aboutItem);

        // Добавляем все меню в менюбар
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private static void showControlsDialog() {
        String controlsText =
                "<html><div style='width:400px;'>" +
                        "<h2>Управление КосмоКомпьютером</h2>" +
                        "<p><b>Мышка:</b></p>" +
                        "<ul>" +
                        "<li>Зажать левую кнопку + двигать - вращение</li>" +
                        "<li>Колесико - масштабирование</li>" +
                        "</ul>" +
                        "<p><b>Кнопки управления:</b></p>" +
                        "<ul>" +
                        "<li>Сбросить вид - возврат к начальному положению</li>" +
                        "<li>Сетка координат - показать/скрыть сетку</li>" +
                        "<li>+/- - увеличение/уменьшение масштаба</li>" +
                        "<li>Стрелки - вращение вручную</li>" +
                        "</ul>" +
                        "<p><b>Горячие клавиши:</b></p>" +
                        "<ul>" +
                        "<li>Ctrl+R - сбросить вид</li>" +
                        "<li>Ctrl+= - приблизить</li>" +
                        "<li>Ctrl+- - отдалить</li>" +
                        "</ul>" +
                        "</div></html>";

        JOptionPane.showMessageDialog(null, controlsText,
                "Управление программой", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showAboutDialog() {
        String aboutText =
                "<html><div style='width:350px;'>" +
                        "<center>" +
                        "<h2 style='color:#4A90E2;'>🌌 КосмоКомпьютер</h2>" +
                        "<p><b>Исследователь Вселенной</b></p>" +
                        "</center>" +
                        "<hr>" +
                        "<p>Интерактивный симулятор звёздного неба,<br>" +
                        "вдохновлённый книгой Стивена Хокинга<br>" +
                        "<i>'Джордж и тайны вселенной'</i></p>" +
                        "<p>Использует реальные данные о звёздах<br>" +
                        "из астрономических каталогов</p>" +
                        "<hr>" +
                        "<p><small>Версия 1.0 (учебный проект)<br>" +
                        "© 2025 Разработано в рамках обучения Java</small></p>" +
                        "</div></html>";

        JOptionPane.showMessageDialog(null, aboutText,
                "О программе", JOptionPane.INFORMATION_MESSAGE);
    }
}

