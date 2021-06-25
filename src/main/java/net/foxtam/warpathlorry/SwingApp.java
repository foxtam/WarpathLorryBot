package net.foxtam.warpathlorry;

import net.foxtam.foxclicker.GlobalLogger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
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
    private final Stopwatch timer;
    private final JButton copyIdButton;
    private final JLabel pauseLabel;
    private final JLabel licenseLabel;

    private String runButtonTitle;

    public SwingApp() {
        super("Warpath Bot - " + App.getAppCurrentVersion());
        enter();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(290, 360);
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

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1, 10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        infoLabel = new JLabel();
        mainPanel.add(infoLabel);

        JLabel IDLabel = new JLabel("ID: " + Computer.getID());
        mainPanel.add(IDLabel);

        copyIdButton = new JButton();
        copyIdButton.addActionListener(this::clipBoardListener);
        mainPanel.add(copyIdButton);

        pauseLabel = new JLabel();
        pauseLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        mainPanel.add(pauseLabel);

        pauseTextField = new JTextField("1.0");
        pauseTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        mainPanel.add(pauseTextField);

        runButton = new JButton("Wait server response...");
        runButton.addActionListener(this::botWorker);
        runButton.setEnabled(false);
        mainPanel.add(runButton);

        licenseLabel = new JLabel();
        mainPanel.add(licenseLabel);

        JLabel timerLabel = new JLabel();
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        mainPanel.add(timerLabel);

        add(mainPanel);

        englishMenuItem.addActionListener(e -> setupEnglishGUI());
        russianMenuItem.addActionListener(e -> setupRussianGUI());

        timer = new Stopwatch(time -> timerLabel.setText(time.toString()));

        setupEnglishGUI();
        setVisible(true);
        SwingUtilities.invokeLater(this::setupGUIWithPermission);
        SwingUtilities.invokeLater(this::checkNewVersion);
        exit();
    }

    private void clipBoardListener(ActionEvent e) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(Computer.getID().toString());
        clipboard.setContents(selection, selection);
    }

    private void botWorker(ActionEvent e) {
        double pauseInMinutes = Double.parseDouble(pauseTextField.getText());
        new BotThread(
                pauseInMinutes,
                timer::switchState,
                timer::stop,
                () -> {
                    timer.restart();
                    runButton.setEnabled(false);
                },
                () -> {
                    timer.stop();
                    runButton.setEnabled(true);
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
        } catch (Exception e) {
            GlobalLogger.trace(e);
            App.showErrorMessage(e.getMessage());
            System.exit(1);
        }
    }

    private void checkNewVersion() {
        try {
            tryCheckNewVersion();
        } catch (Exception e) {
            GlobalLogger.trace(e);
            App.showErrorMessage(e.getMessage());
        }
    }

    private void trySetupGUI() throws IOException {
        Registration registration = WarpathServer.getRegistrationInfoFor(Computer.getID());
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

    private void tryCheckNewVersion() {
        Version lastVersion = WarpathServer.getBotLastVersion();
        Version currentVersion = App.getAppCurrentVersion();

        if (lastVersion.isGreater(currentVersion)) {
            JOptionPane.showMessageDialog(
                    this,
                    "New version is available",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void initRunnableGUI(LocalDate expirationDate) {
        licenseLabel.setText("License is valid until: " + expirationDate);
        licenseLabel.setForeground(Color.BLUE);
        runButton.setEnabled(true);
    }

    private void initExpiredDateGUI(LocalDate expirationDate) {
        licenseLabel.setText("License expired: " + expirationDate);
        licenseLabel.setForeground(Color.RED);
        runButton.setEnabled(false);
    }

    private void initNoRunGUI() {
        licenseLabel.setText("Client ID unknown");
        licenseLabel.setForeground(Color.RED);
        runButton.setEnabled(false);
    }
}
