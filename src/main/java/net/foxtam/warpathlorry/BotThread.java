package net.foxtam.warpathlorry;

import net.foxtam.foxclicker.GlobalLogger;
import net.foxtam.warpathlorry.bot.WarpathBot;

public class BotThread extends Thread {
    private final double bypassPauseInMinutes;
    private final double alreadyLoggedPauseInMinutes;
    private final Runnable onStop;
    private final Runnable onPause;
    private final Runnable runBefore;
    private final Runnable runAfter;

    public BotThread(double bypassPauseInMinutes, double alreadyLoggedPauseInMinutes, Runnable onPause, Runnable onStop, Runnable runBefore, Runnable runAfter) {
        this.bypassPauseInMinutes = bypassPauseInMinutes;
        this.alreadyLoggedPauseInMinutes = alreadyLoggedPauseInMinutes;
        this.onStop = onStop;
        this.onPause = onPause;
        this.runBefore = runBefore;
        this.runAfter = runAfter;
    }

    @Override
    public void run() {
        try {
            runBefore.run();
            new WarpathBot(bypassPauseInMinutes, alreadyLoggedPauseInMinutes, onStop, onPause).run();
        } catch (Exception e) {
            GlobalLogger.trace(e);
            App.showErrorMessage(e.getMessage());
        } finally {
            runAfter.run();
        }
    }
}
