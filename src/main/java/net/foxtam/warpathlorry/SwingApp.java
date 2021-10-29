package net.foxtam.warpathlorry;

import net.foxtam.foxclicker.GlobalLogger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

import static net.foxtam.foxclicker.GlobalLogger.*;

public class SwingApp extends JFrame {

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final JButton startBotButton;
    private final JLabel infoLabel;
    private final JTextField bypassPauseTextField;
    private final Stopwatch stopwatch;
    private final JButton copyIdButton;
    private final JLabel bypassPauseLabel;
    private final JLabel licenseLabel;
    private final JLabel alreadyLoggedPauseLabel;
    private final JTextField alreadyLoggedPauseTextField;

    private String runButtonTitle;
    private String clientIdUnknownText;
    private String licenseIsValidText;
    private String licenseExpiredText;

    public SwingApp() {
        super("Warpath Bot - " + App.getAppCurrentVersion());
        enter();
        trace("Bot version: " + App.getAppCurrentVersion());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 420);
        setLocationRelativeTo(null);
//        setResizable(false);

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

        bypassPauseLabel = new JLabel();
        bypassPauseLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        mainPanel.add(bypassPauseLabel);

        bypassPauseTextField = new JTextField("1.0");
        bypassPauseTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        mainPanel.add(bypassPauseTextField);

        alreadyLoggedPauseLabel = new JLabel();
        mainPanel.add(alreadyLoggedPauseLabel);

        alreadyLoggedPauseTextField = new JTextField("1.0");
        alreadyLoggedPauseTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        mainPanel.add(alreadyLoggedPauseTextField);

        startBotButton = new JButton("Wait server response...");
        startBotButton.addActionListener(this::botWorker);
        startBotButton.setEnabled(false);
        mainPanel.add(startBotButton);

        licenseLabel = new JLabel();
        mainPanel.add(licenseLabel);

        JLabel timerLabel = new JLabel();
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        mainPanel.add(timerLabel);

        add(mainPanel);

        englishMenuItem.addActionListener(e -> setupEnglishGUI());
        russianMenuItem.addActionListener(e -> setupRussianGUI());

        stopwatch = new Stopwatch(time -> timerLabel.setText(time.toString()));

        setupEnglishGUI();
        setVisible(true);
        SwingUtilities.invokeLater(this::setupGUIWithPermission);
        SwingUtilities.invokeLater(this::checkNewVersion);

        JOptionPane.showMessageDialog(
                this,
                "Демо версия завершит работу через 20 минут",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
        new Thread(new KillBot()).start();
        
        exit();
    }

    private void clipBoardListener(ActionEvent e) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(Computer.getID().toString());
        clipboard.setContents(selection, selection);
    }

    private void botWorker(ActionEvent e) {
        double bypassPauseInMinutes = Double.parseDouble(bypassPauseTextField.getText());
        double alreadyLoggedPauseInMinutes = Double.parseDouble(alreadyLoggedPauseTextField.getText());
        new BotThread(
                bypassPauseInMinutes,
                alreadyLoggedPauseInMinutes,
                stopwatch::switchState,
                stopwatch::stop,
                () -> {
                    stopwatch.restart();
                    startBotButton.setEnabled(false);
                },
                () -> {
                    stopwatch.stop();
                    startBotButton.setEnabled(true);
                }
        ).start();
    }

    private void setupEnglishGUI() {
        infoLabel.setText("<html>Pause bot on/off: F4<br>Stop bot: F8</html>");
        copyIdButton.setText("Copy ID");
        bypassPauseLabel.setText("Pause between cycles (minutes):");
        alreadyLoggedPauseLabel.setText("Pause after \"Already logged...\" message (minutes):");
        runButtonTitle = "Start";
        startBotButton.setText(runButtonTitle);

    }

    private void setupRussianGUI() {
        infoLabel.setText("<html>Установить/снять паузу: F4<br>Остановить бота: F8</html>");
        copyIdButton.setText("Копировать ID");
        bypassPauseLabel.setText("Пауза между обходами (в минутах):");
        alreadyLoggedPauseLabel.setText("Пауза после сообщения о входе на другом устройстве (в минутах):");
        runButtonTitle = "Старт";
        startBotButton.setText(runButtonTitle);
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

    private void trySetupGUI() {
        Registration registration = WarpathServer.getRegistrationInfoFor(Computer.getID());
        if (registration.hasRegistration()) {
            setupWithRegistration(registration);
        } else {
            initNoRunGUI();
        }
        startBotButton.setText(runButtonTitle);
    }

    private void tryCheckNewVersion() {
        Version lastVersion = WarpathServer.getBotLastVersion();
        Version currentVersion = App.getAppCurrentVersion();

        if (lastVersion.isGreater(currentVersion)) {
            JOptionPane.showMessageDialog(
                    this,
                    "New version is available: " + lastVersion,
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setupWithRegistration(Registration registration) {
        LocalDate expirationLicenseDate = registration.getExpirationLicenseDate();
        if (registration.isLicenseValid()) {
            initRunnableGUI(expirationLicenseDate);
        } else {
            initExpiredDateGUI(expirationLicenseDate);
        }
    }

    private void initNoRunGUI() {
        licenseLabel.setText("Client ID unknown");
        licenseLabel.setForeground(Color.RED);
        startBotButton.setEnabled(false);
    }

    private void initRunnableGUI(LocalDate expirationDate) {
        licenseLabel.setText("License is valid until: " + expirationDate);
        licenseLabel.setForeground(Color.BLUE);
        startBotButton.setEnabled(true);
    }

    private void initExpiredDateGUI(LocalDate expirationDate) {
        licenseLabel.setText("License expired: " + expirationDate);
        licenseLabel.setForeground(Color.RED);
        startBotButton.setEnabled(false);
    }
}
