package net.foxtam.warpathlorry;

import net.foxtam.foxclicker.exceptions.WaitForImageException;
import net.foxtam.warpathlorry.bot.WarpathBot;

import javax.swing.*;

public class BotThread extends Thread {
    private final JButton button;

    private final double pauseInMinutes;

    private final Runnable onStop;

    private final Runnable onPause;

    public BotThread(JButton button, double pauseInMinutes, Runnable onStop, Runnable onPause) {
        this.button = button;
        this.pauseInMinutes = pauseInMinutes;
        this.onStop = onStop;
        this.onPause = onPause;
    }

    @Override
    public void run() {
        try {
            this.button.setEnabled(false);
            (new WarpathBot(this.pauseInMinutes, this.onStop, this.onPause)).run();
        } catch (WaitForImageException exception) {
            showErrorMessage(exception);
        } finally {
            this.button.setEnabled(true);
        }
    }

    private void showErrorMessage(WaitForImageException exception) {
        JOptionPane.showMessageDialog(
              null,
              exception.getMessage(),
              "Image not found",
              JOptionPane.ERROR_MESSAGE);
    }
}
