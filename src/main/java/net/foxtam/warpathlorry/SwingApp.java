package net.foxtam.warpathlorry;

import net.foxtam.warpathlorry.window.BotFrame;

import javax.swing.*;

public class SwingApp {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(BotFrame::new);
    }
}
