package net.foxtam.warpathlorry.window;

import javax.swing.*;
import java.awt.*;

public class BotFrame extends JFrame {

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BotFrame() {
        super("Warpath Lorry Bot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 320);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(panel);

        JLabel helloLabel = new JLabel("Hello");
        panel.add(helloLabel);

        JCheckBox checkBox = new JCheckBox("CheckBox");
        panel.add(checkBox);

        JTextField jTextField = new JTextField("Empty");
        panel.add(jTextField);

        JButton runButton = new JButton("Run");
        runButton.addActionListener(e -> System.out.println("Hello"));
        panel.add(runButton);
        
        setVisible(true);
    }
}
