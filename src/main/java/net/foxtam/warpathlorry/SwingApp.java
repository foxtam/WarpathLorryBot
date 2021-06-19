package net.foxtam.warpathlorry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

import static net.foxtam.foxclicker.GlobalLogger.enter;
import static net.foxtam.foxclicker.GlobalLogger.exit;

public class SwingApp extends JFrame {

    private static final boolean LOCAL_TEST = false;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final String clientID = IDCalculator.getCurrentMachineID();
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

        JLabel IDLabel = new JLabel("ID: " + clientID);
        idPanel.add(IDLabel);

        copyIdButton = new JButton();
        copyIdButton.addActionListener(getClipboardListener());
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
        runButton.addActionListener(getBotWorker());
        runButton.setEnabled(false);
        runPanel.add(runButton);

        botTimer = new BotTimer();
        JLabel timerLabel = new JLabel(botTimer.toString());
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        runPanel.add(timerLabel);

        add(panel);

        timer = new Timer(
              1000,
              e -> {
                  botTimer.addSecond();
                  timerLabel.setText(botTimer.toString());
              });

        setEnglishGUI();

        englishMenuItem.addActionListener(getEnglishGUIListener());
        russianMenuItem.addActionListener(getRussianGUIListener());

        setVisible(true);
        SwingUtilities.invokeLater(permissionCheck());
        runDemoMode();
        exit();
    }

    private ActionListener getClipboardListener() {
        return e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(clientID);
            clipboard.setContents(selection, selection);
        };
    }

    private ActionListener getBotWorker() {
        return e -> {
            botTimer.resetTime();
            timer.start();
            new BotThread(
                  runButton,
                  Double.parseDouble(pauseTextField.getText()),
                  timer::stop,
                  () -> {
                      if (timer.isRunning()) {
                          timer.stop();
                      } else {
                          timer.start();
                      }
                  }
            ).start();
        };
    }

    private void setEnglishGUI() {
        infoLabel.setText("<html>Pause bot on/off: F4<br>Stop bot: F8</html>");
        copyIdButton.setText("Copy ID");
        pauseLabel.setText("Pause between cycles (minutes):");
        runButtonTitle = "Start";
        runButton.setText(runButtonTitle);
    }

    private ActionListener getEnglishGUIListener() {
        return e -> setEnglishGUI();
    }

    private ActionListener getRussianGUIListener() {
        return e -> setRussianGUI();
    }

    private Runnable permissionCheck() {
        return () -> {
            Optional<LocalDate> optionalDate = getClientExpirationDate(clientID);

            if (optionalDate.isPresent()) {
                LocalDate expirationDate = optionalDate.get();
                LocalDate now = getGlobalTime();
                if (now.isBefore(expirationDate)) {
                    initRunnableGUI(expirationDate);
                } else {
                    initExpiredDateGUI(expirationDate);
                }
            } else {
                initNoRunGUI();
            }
            runButton.setText(runButtonTitle);
        };
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

    private void setRussianGUI() {
        infoLabel.setText("<html>Установить/снять паузу: F4<br>Остановить бота: F8</html>");
        copyIdButton.setText("Копировать ID");
        pauseLabel.setText("Пауза между обходами (в минутах):");
        runButtonTitle = "Старт";
        runButton.setText(runButtonTitle);
    }

    private Optional<LocalDate> getClientExpirationDate(String clientID) {
        try {
            return tryGetClientExpirationDate(clientID);
        } catch (ConnectException e) {
            showErrorMessage("No server connection!");
            throw new RuntimeException(e);
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private LocalDate getGlobalTime() {
        try {
            URL url = new URL("https://currentmillis.com/time/minutes-since-unix-epoch.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            long minutes = Long.parseLong(in.readLine());
            in.close();
            con.disconnect();
            Instant instant = Instant.ofEpochSecond(minutes * 60);
            return LocalDate.ofInstant(instant, ZoneId.of("UTC+0"));
        } catch (IOException e) {
            showErrorMessage(e.getMessage());
            throw new RuntimeException(e);
        }
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

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
              null,
              message,
              "Error",
              JOptionPane.ERROR_MESSAGE);
    }

    private Optional<LocalDate> tryGetClientExpirationDate(String clientID) throws IOException {
        Gson gson = new Gson();

        java.lang.reflect.Type type =
              new TypeToken<Map<String, String>>() {
              }.getType();

        Map<String, String> map =
              gson.fromJson(
                    new InputStreamReader(getClientsFileInputStream(), StandardCharsets.UTF_8),
                    type);

        return Optional.ofNullable(map.get(clientID)).map(LocalDate::parse);
    }

    private InputStream getClientsFileInputStream() throws IOException {
        if (LOCAL_TEST) {
            String clientsFile = "warpath_clients.json";
            return new BufferedInputStream(new FileInputStream(clientsFile));
        } else {
            String clientsFile = "https://garantmarket.net/warpath/warpath_clients.json";
            URL url = new URL(clientsFile);
            return new BufferedInputStream(url.openStream());
        }
    }
}
