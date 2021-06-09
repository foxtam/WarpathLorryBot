package net.foxtam.warpathlorry;

import net.foxtam.warpathlorry.bot.WarpathBot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class SwingApp extends JFrame {

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
    }

    private final String ID = IDCalculator.getCurrentMachineID();

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(SwingApp::new);
    }

    public SwingApp() {
        super("Warpath Bot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(280, 240);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String message = String.format("<html>Pause bot: F4<br>Stop bot: F8<br><br>ID: %s</html>", ID);
        JLabel label = new JLabel(message);
        panel.add(label);

        JButton copyIdButton = new JButton("Copy ID");
        copyIdButton.addActionListener(
            e -> {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(ID);
                clipboard.setContents(selection, selection);
            });
        panel.add(copyIdButton);

        JButton runButton = new JButton("Run");
        runButton.addActionListener(e -> {
            runButton.setEnabled(false);
            new WarpathBot().run();
            runButton.setEnabled(true);
        });
        panel.add(runButton);

        add(panel);
//        pack();
        setVisible(true);
    }
}
