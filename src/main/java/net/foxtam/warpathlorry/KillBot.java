package net.foxtam.warpathlorry;

import lombok.SneakyThrows;

public class KillBot implements Runnable {
    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(20 * 60 * 1000);
        System.exit(1);
    }
}
