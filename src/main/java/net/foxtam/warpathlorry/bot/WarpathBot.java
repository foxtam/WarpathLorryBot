package net.foxtam.warpathlorry.bot;


import net.foxtam.foxclicker.*;

import java.util.Random;

public class WarpathBot extends Bot {

    final Image lorryMainButton = Image.loadFromResource("/images/lorry_main_button.png");
    final Image sendLorryButton = Image.loadFromResource("/images/send_lorry_button.png");
    final Image recallLorryButton = Image.loadFromResource("/images/recall_lorry_button.png");
    final Image lorry = Image.loadFromResource("/images/lorry.png");
    final Image sleepingLorryIcon = Image.loadFromResource("/images/sleeping_lorry_icon.png");
    final Image farm = Image.loadFromResource("/images/farm.png");
    final Image mine = Image.loadFromResource("/images/mine.png");
    final Image oilWell = Image.loadFromResource("/images/oil_well.png");
    final Image searchButton = Image.loadFromResource("/images/search_button.png");
    final Image roundSendLorryButton = Image.loadFromResource("/images/round_send_lorry_button.png");

    private final Random random = new Random();

    private Direction lorryDragDirection = Direction.LEFT;

    public WarpathBot() {
        super(KeyConfig.getDefault(), Window.getByTitle("NoxPlayer"));
    }

    @Override
    protected void action() {
        while (true) {
            bypass();
            sleep(5);
        }
    }

    private void bypass() {
        openBottomLorryWindow();
        if (!canSeeSleepingLorry()) showSleepingLorry();
        if (!canSeeSleepingLorry()) return;
        sendSleepingLorry();
    }

    private void openBottomLorryWindow() {
        for (int i = 0; i < 2; i++) {
            if (!isImageVisible(lorry)) {
                leftClickOn(lorryMainButton);
                sleep(1);
            }
        }
        waitForImage(lorry, 3);
    }

    private boolean canSeeSleepingLorry() {
        return isImageVisible(sleepingLorryIcon);
    }

    private void showSleepingLorry() {
        mouseMoveTo(lorry);
        mouseDragDirection(lorryDragDirection, 300);
        lorryDragDirection =
            lorryDragDirection == Direction.LEFT
                ? Direction.RIGHT
                : Direction.LEFT;
    }

    private void sendSleepingLorry() {
        leftClickOn(sleepingLorryIcon);
        waitForImage(sendLorryButton, 5);
        leftClickOn(sendLorryButton);
        chooseDestinationType();
        leftClickOn(searchButton);
        sleep(2);
        leftClickAt(getWindowCenterPoint());
        waitForImage(roundSendLorryButton, 5);
        leftClickOn(roundSendLorryButton);
    }

    private void chooseDestinationType() {
        Image[] destinations = {farm, mine, oilWell};
        Image choice;
        do {
            choice = destinations[random.nextInt(destinations.length)];
        } while (!isImageVisible(choice));
        leftClickOn(choice);
    }
}
