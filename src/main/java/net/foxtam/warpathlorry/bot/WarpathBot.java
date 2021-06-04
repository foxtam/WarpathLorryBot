package net.foxtam.warpathlorry.bot;


import net.foxtam.foxclicker.Bot;
import net.foxtam.foxclicker.KeyConfig;

public class WarpathBot extends Bot {
    public WarpathBot() {
        super("NoxPlayer", KeyConfig.getDefault());
    }

    @Override
    protected void action() {
        System.out.println("Hello!");
    }
}
