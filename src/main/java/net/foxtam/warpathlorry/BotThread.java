package net.foxtam.warpathlorry;

import net.foxtam.foxclicker.GlobalLogger;
import net.foxtam.warpathlorry.bot.WarpathBot;

import javax.swing.*;

public class BotThread extends Thread {
    private final double pauseInMinutes;
    private final Runnable onStop;
    private final Runnable onPause;
    private final Runnable runBefore;
    private final Runnable runAfter;

    public BotThread(double pauseInMinutes, Runnable onPause, Runnable onStop, Runnable runBefore, Runnable runAfter) {
        this.pauseInMinutes = pauseInMinutes;
        this.onStop = onStop;
        this.onPause = onPause;
        this.runBefore = runBefore;
        this.runAfter = runAfter;
    }

    @Override
    public void run() {
        try {
            runBefore.run();
            new WarpathBot(this.pauseInMinutes, this.onStop, this.onPause).run();
        } catch (Exception e) {
            GlobalLogger.trace(e);
            App.showErrorMessage(e.getMessage());
        } finally {
            runAfter.run();
        }
    }
}
