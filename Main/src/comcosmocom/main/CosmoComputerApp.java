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
}