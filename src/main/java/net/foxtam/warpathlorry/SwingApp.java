package net.foxtam.warpathlorry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDate;

import static net.foxtam.foxclicker.GlobalLogger.enter;
import static net.foxtam.foxclicker.GlobalLogger.exit;

public class SwingApp extends JFrame {

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final JButton runButton;
    private final JLabel infoLabel;
    private final JTextField pauseTextField;
    private final Timer timer;
    private final BotTimer botTimer;
    private final JButton copyIdButton;
    private final JLabel pauseLabel;

    private String runButtonTitle;

    public static void main(String[] args) {
        enter((Object[]) args);
        try {
            SwingUtilities.invokeAndWait(SwingApp::new);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        exit();
    }

    public SwingApp() {
        super("Демо версия - Warpath Bot");
        enter();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(280, 340);
        setLocationRelativeTo(null);
        setResizable(false);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu languageMenu = new JMenu("Language / Язык");
        menuBar.add(languageMenu);

        JMenuItem englishMenuItem = new JMenuItem("English");
        JMenuItem russianMenuItem = new JMenuItem("Русский");

        languageMenu.add(englishMenuItem);
        languageMenu.add(russianMenuItem);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel idPanel = new JPanel();
        idPanel.setLayout(new GridLayout(0, 1, 10, 10));

        infoLabel = new JLabel();
        panel.add(infoLabel);

        JLabel IDLabel = new JLabel("ID: " + Computer.getID());
        idPanel.add(IDLabel);

        copyIdButton = new JButton();
        copyIdButton.addActionListener(this::clipBoardListener);
        idPanel.add(copyIdButton);

        panel.add(idPanel);

        JPanel pausePanel = new JPanel();
        pausePanel.setLayout(new GridLayout(0, 1, 10, 10));

        pauseLabel = new JLabel();
        pauseLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        pausePanel.add(pauseLabel);

        pauseTextField = new JTextField("1.0");
        pauseTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        pausePanel.add(pauseTextField);

        panel.add(pausePanel);

        JPanel runPanel = new JPanel();
        runPanel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.add(runPanel);

        runButton = new JButton("Wait server response...");
        runButton.addActionListener(this::botWorker);
        runButton.setEnabled(false);
        runPanel.add(runButton);

        botTimer = new BotTimer();
        JLabel timerLabel = new JLabel(botTimer.toString());
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        runPanel.add(timerLabel);

        add(panel);

        englishMenuItem.addActionListener(e -> setupEnglishGUI());
        russianMenuItem.addActionListener(e -> setupRussianGUI());

        timer = new Timer(
              1000,
              e -> {
                  botTimer.addSecond();
                  timerLabel.setText(botTimer.toString());
              });
        
        setupEnglishGUI();
        setVisible(true);
        SwingUtilities.invokeLater(this::setupGUIWithPermission);
//        runDemoMode();
        exit();
    }

    private void clipBoardListener(ActionEvent e) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(Computer.getID().toString());
        clipboard.setContents(selection, selection);
    }

    private void botWorker(ActionEvent e) {
        botTimer.resetTime();
        timer.start();
        double pauseInMinutes = Double.parseDouble(pauseTextField.getText());
        new BotThread(
              runButton,
              pauseInMinutes,
              timer::stop,
              () -> {
                  if (timer.isRunning()) {
                      timer.stop();
                  } else {
                      timer.start();
                  }
              }
        ).start();
    }

    private void setupEnglishGUI() {
        infoLabel.setText("<html>Pause bot on/off: F4<br>Stop bot: F8</html>");
        copyIdButton.setText("Copy ID");
        pauseLabel.setText("Pause between cycles (minutes):");
        runButtonTitle = "Start";
        runButton.setText(runButtonTitle);
    }

    private void setupRussianGUI() {
        infoLabel.setText("<html>Установить/снять паузу: F4<br>Остановить бота: F8</html>");
        copyIdButton.setText("Копировать ID");
        pauseLabel.setText("Пауза между обходами (в минутах):");
        runButtonTitle = "Старт";
        runButton.setText(runButtonTitle);
    }

    private void setupGUIWithPermission() {
        try {
            trySetupGUI();
        } catch (IOException e) {
            showErrorMessage(e.getMessage());
            System.exit(1);
        }
    }

    private void runDemoMode() {
        Thread thread = new Thread(() -> {
            try {
                JOptionPane.showMessageDialog(
                      null,
                      "Демо версия завершит работу через 15 минут");
                Thread.sleep(900 * 1000);
                System.exit(0);
            } catch (InterruptedException e) {
                showErrorMessage(e.getMessage());
                throw new RuntimeException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void trySetupGUI() throws IOException {
        Registration registration = new Registration();
        if (registration.hasRegistration()) {
            LocalDate expirationLicenseDate = registration.getExpirationLicenseDate();
            if (registration.isLicenseValid()) {
                initRunnableGUI(expirationLicenseDate);
            } else {
                initExpiredDateGUI(expirationLicenseDate);
            }
        } else {
            initNoRunGUI();
        }
        runButton.setText(runButtonTitle);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void initRunnableGUI(LocalDate expirationDate) {
        JOptionPane.showMessageDialog(
              this,
              "License is valid until: " + expirationDate,
              "Info",
              JOptionPane.INFORMATION_MESSAGE);
        infoLabel.setForeground(Color.BLUE);
        runButton.setEnabled(true);
    }

    private void initExpiredDateGUI(LocalDate expirationDate) {
        JOptionPane.showMessageDialog(
              this,
              "License expired: " + expirationDate,
              "Warning",
              JOptionPane.WARNING_MESSAGE);
        infoLabel.setForeground(Color.RED);
        runButton.setEnabled(false);
    }

    private void initNoRunGUI() {
        JOptionPane.showMessageDialog(
              this,
              "Client ID unknown",
              "Warning",
              JOptionPane.WARNING_MESSAGE);
        infoLabel.setForeground(Color.RED);
        runButton.setEnabled(false);
    }
}
