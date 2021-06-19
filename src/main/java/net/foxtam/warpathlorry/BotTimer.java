package net.foxtam.warpathlorry;

import java.time.LocalTime;

public class BotTimer {
    private LocalTime timer = LocalTime.of(0, 0);

    public void addSecond() {
        this.timer = this.timer.plusSeconds(1L);
    }

    @Override
    public String toString() {
        return this.timer.toString();
    }

    public void resetTime() {
        this.timer = LocalTime.of(0, 0);
    }
}
