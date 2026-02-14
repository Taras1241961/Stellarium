package src.comcosmocom.main;
import src.comcosmocom.gui.StarSpherePanel;
import javax.swing.*;
import java.awt.*;

public class CosmoComputerApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("КосмоКомпьютер - Планетарий");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 1000);
            frame.setLocationRelativeTo(null);

            StarSpherePanel starPanel = new StarSpherePanel();
            frame.add(starPanel, BorderLayout.CENTER);

            JPanel controlPanel = createControlPanel(starPanel);
            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.setJMenuBar(createMenuBar(starPanel));
            frame.setVisible(true);
        });
    }

    private static JPanel createControlPanel(StarSpherePanel panel) {
        JPanel panelControls = new JPanel();
        panelControls.setBackground(new Color(20, 20, 40));

        JButton resetButton = new JButton("Сброс");
        resetButton.addActionListener(e -> panel.resetView());

        JToggleButton gridButton = new JToggleButton("Сетка", panel.isGridVisible());
        gridButton.addActionListener(e -> panel.setGridVisible(gridButton.isSelected()));

        JToggleButton constButton = new JToggleButton("Созвездия", panel.isConstellationsVisible());
        constButton.addActionListener(e -> panel.setConstellationsVisible(constButton.isSelected()));

        JToggleButton labelButton = new JToggleButton("Подписи", panel.isLabelsVisible());
        labelButton.addActionListener(e -> panel.setLabelsVisible(labelButton.isSelected()));

        panelControls.add(resetButton);
        panelControls.add(gridButton);
        panelControls.add(constButton);
        panelControls.add(labelButton);

        return panelControls;
    }

    private static JMenuBar createMenuBar(StarSpherePanel panel) {
        JMenuBar menuBar = new JMenuBar();

        JMenu viewMenu = new JMenu("Вид");

        JCheckBoxMenuItem gridItem = new JCheckBoxMenuItem("Сетка координат", panel.isGridVisible());
        gridItem.addActionListener(e -> panel.setGridVisible(gridItem.isSelected()));

        JCheckBoxMenuItem constItem = new JCheckBoxMenuItem("Созвездия", panel.isConstellationsVisible());
        constItem.addActionListener(e -> panel.setConstellationsVisible(constItem.isSelected()));

        JCheckBoxMenuItem labelItem = new JCheckBoxMenuItem("Подписи звёзд", panel.isLabelsVisible());
        labelItem.addActionListener(e -> panel.setLabelsVisible(labelItem.isSelected()));

        viewMenu.add(gridItem);
        viewMenu.add(constItem);
        viewMenu.add(labelItem);

        menuBar.add(viewMenu);

        return menuBar;
    }
}
