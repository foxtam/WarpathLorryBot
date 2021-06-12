package net.foxtam.warpathlorry;

import net.foxtam.foxclicker.exceptions.WaitForImageException;
import net.foxtam.warpathlorry.bot.WarpathBot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class SwingApp extends JFrame {

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final String clientID = IDCalculator.getCurrentMachineID();
    private final String serverIP;
    private final int serverPort;
    private final JButton runButton;
    private final JLabel infoLabel;
    private final JTextField pauseTextField;

    {
        try {
            serverIP = BotProperties.getServerIP();
            serverPort = BotProperties.getServerPort();
        } catch (CantReadPropertiesException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(SwingApp::new);
    }

    public SwingApp() {
        super("Демо версия - Warpath Bot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(280, 320);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String message = String.format("<html>Pause bot: F4<br>Stop bot: F8<br><br>ID: %s</html>", clientID);
        infoLabel = new JLabel(message);
        panel.add(infoLabel);

        JButton copyIdButton = new JButton("Copy ID");
        copyIdButton.addActionListener(getClipboardListener());
        panel.add(copyIdButton);

        JPanel pausePanel = new JPanel();
        pausePanel.setLayout(new GridLayout(0, 1, 10, 10));

        JLabel pauseLabel = new JLabel("Pause between cycles (minutes):");
        pauseLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        pausePanel.add(pauseLabel);

        pauseTextField = new JTextField("1.0");
        pauseTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        pausePanel.add(pauseTextField);

        panel.add(pausePanel);

        runButton = new JButton("Wait server response...");
        runButton.addActionListener(getRunBotListener());
        runButton.setEnabled(false);
        panel.add(runButton);

        add(panel);

        setVisible(true);
        SwingUtilities.invokeLater(communicateToServer());
        runDemoMode();
    }

    private ActionListener getClipboardListener() {
        return e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(clientID);
            clipboard.setContents(selection, selection);
        };
    }

    private ActionListener getRunBotListener() {
        return e -> {
            try {
                runButton.setEnabled(false);
                double pauseInMinutes = Double.parseDouble(pauseTextField.getText());
                new WarpathBot(pauseInMinutes).run();
            } catch (WaitForImageException exception) {
                JOptionPane.showMessageDialog(
                      this,
                      exception.getMessage(),
                      "Image not found",
                      JOptionPane.ERROR_MESSAGE);
            } finally {
                runButton.setEnabled(true);
            }
        };
    }

    private Runnable communicateToServer() {
        return () -> {
            try (
                  Socket socket = new Socket(serverIP, serverPort);
                  DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                  DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
            ) {
                tryCommunicateToServer(dataInputStream, dataOutputStream);
            } catch (ConnectException e) {
                JOptionPane.showMessageDialog(
                      this,
                      "No server connection!",
                      "Warning",
                      JOptionPane.WARNING_MESSAGE);
                runButton.setText("No server connection!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void runDemoMode() {
        new Thread(() -> {
            try {
                JOptionPane.showMessageDialog(
                      null,
                      "Демо версия завершит работу через 5 минут");
                Thread.sleep(60 * 1000);
                System.exit(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void tryCommunicateToServer(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        runButton.setText("Run");
        dataOutputStream.writeUTF(clientID);
        boolean canStart = dataInputStream.readBoolean();
        if (canStart) {
            initRunnableGUI(dataInputStream.readUTF());
        } else {
            initNoRunGUI(dataInputStream.readUTF());
        }
        runButton.setEnabled(canStart);
    }

    private void initRunnableGUI(String expirationDate) {
        JOptionPane.showMessageDialog(
              this,
              "License is valid until: " + expirationDate,
              "Info",
              JOptionPane.INFORMATION_MESSAGE);
        infoLabel.setForeground(Color.BLUE);
    }

    private void initNoRunGUI(String expirationDate) {
        JOptionPane.showMessageDialog(
              this,
              expirationDate.isEmpty()
                    ? "Client ID unknown"
                    : "License expired: " + expirationDate,
              "Warning",
              JOptionPane.WARNING_MESSAGE);
        infoLabel.setForeground(Color.RED);
    }
}
