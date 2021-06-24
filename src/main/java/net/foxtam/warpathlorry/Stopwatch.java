package net.foxtam.warpathlorry;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Consumer;

public class Stopwatch {

    private final Timer timer;
    private final Consumer<LocalTime> consumer;
    private LocalTime init;

    public Stopwatch(Consumer<LocalTime> consumer) {
        this.consumer = consumer;
        this.timer = new Timer(1000, this::timerListener);
    }

    private void timerListener(ActionEvent event) {
        Duration between = Duration.between(init, LocalTime.now());
        consumer.accept(
                LocalTime.of(
                        between.toHoursPart(),
                        between.toMinutesPart(),
                        between.toSecondsPart()));
    }

    public void switchState() {
        if (timer.isRunning()) {
            stop();
        } else {
            start();
        }
    }

    public void stop() {
        timer.stop();
    }

    public void start() {
        if (init == null) init = LocalTime.now();
        timer.start();
    }

    public void reset() {
        init = LocalTime.now();
    }
}
