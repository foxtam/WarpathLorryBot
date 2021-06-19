package net.foxtam.warpathlorry.bot;


import net.foxtam.foxclicker.*;

import java.util.List;
import java.util.Random;

import static net.foxtam.foxclicker.GlobalLogger.enter;
import static net.foxtam.foxclicker.GlobalLogger.exit;

public class WarpathBot extends Bot implements Runnable {

    final Image lorryMainButton = Image.loadFromResource("/images/lorry_main_button.png");
    final Image deployButton = Image.loadFromResource("/images/deploy_button.png");
    final Image lorry = Image.loadFromResource("/images/lorry.png");
    final Image sleepingLorryIcon1 = Image.loadFromResource("/images/sleeping_lorry_icon_1.png");
    final Image sleepingLorryIcon2 = Image.loadFromResource("/images/sleeping_lorry_icon_2.png");
    final Image farm = Image.loadFromResource("/images/farm.png");
    final Image mine = Image.loadFromResource("/images/mine.png");
    final Image oilWell = Image.loadFromResource("/images/oil_well.png");
    final Image searchButton = Image.loadFromResource("/images/search_button.png");
    final Image dispatchLorryButton = Image.loadFromResource("/images/dispatch_lorry_button.png");
    final Image returnToBaseButton = Image.loadFromResource("/images/return_to_base_button.png");
    final Image produceRoundButton = Image.loadFromResource("/images/produce_round_button.png");
    final Image produceGreenButton = Image.loadFromResource("/images/produce_green_button.png");
    final Image productLeftArrows = Image.loadFromResource("/images/product_left_arrows.png");
    final Image productRightArrows = Image.loadFromResource("/images/product_right_arrows.png");
    final Image whiteSlash = Image.loadFromResource("/images/white_slash.png");
    final Image backToMainScreenButton = Image.loadFromResource("/images/back_to_main_screen_button.png");
    final Image recallLorryButton = Image.loadFromResource("/images/recall_lorry_button.png");
    final Image redSlash = Image.loadFromResource("/images/red_slash.png");

    final List<Image[]> workshops =
          List.of(
                new Image[]{
                      Image.loadFromResource("/images/workshop_1_1.png"),
                      Image.loadFromResource("/images/workshop_1_2.png")
                },
                new Image[]{
                      Image.loadFromResource("/images/workshop_2_1.png"),
                      Image.loadFromResource("/images/workshop_2_2.png"),
                },
                new Image[]{
                      Image.loadFromResource("/images/workshop_3_1.png"),
                      Image.loadFromResource("/images/workshop_3_2.png")
                },
                new Image[]{
                      Image.loadFromResource("/images/workshop_4_1.png"),
                      Image.loadFromResource("/images/workshop_4_2.png")
                });


    private final Random random = new Random();
    private final Finder finder = new Finder(4.0, 0.85, false);
    private final double pauseInMinutes;
    private Direction lorryDragDirection = Direction.LEFT;

    public WarpathBot(double pauseInMinutes, Runnable onStop, Runnable onPause) {
        super(KeyConfig.getDefault(), Window.getByTitle("NoxPlayer"), onStop, onPause);
        enter(pauseInMinutes);
        this.pauseInMinutes = pauseInMinutes;
        exit();
    }

    @Override
    protected void action() {
        enter();
        //noinspection InfiniteLoopStatement
        while (true) {
            lorryBypass();
            factoryBypass();
            sleep(pauseInMinutes * 60);
        }
    }

    private void lorryBypass() {
        enter();
        while (true) {
            openBottomLorryWindow();
            Image button = finder.waitForAnyImage(deployButton, recallLorryButton);
            if (button == deployButton) {
                sendSleepingLorry();
            } else if (button == recallLorryButton) {
                hideBottomLorryWindow();
                exit();
                return;
            }
        }
    }

    private void factoryBypass() {
        enter();
        finder.leftClickOn(returnToBaseButton);
        sleep(2.0);
        clickOnFactoryBuilding();
        finder.leftClickOn(produceRoundButton);
        finder.waitForAnyImage(workshops.get(0));

        shiftProductsToRight();
        for (Image[] workshop : workshops) {
            if (finder.isAnyImageVisible(workshop)) {
                tapNextWorkshop(workshop);
                oneTapToRight();
                orderProduct();
            } else //noinspection BreakStatement
                break;
        }

        finder.leftClickOn(backToMainScreenButton);
        exit();
    }

    private void openBottomLorryWindow() {
        enter();
        for (int i = 0; i < 2; i++) {
            if (!finder.isImageVisible(lorry)) {
                finder.leftClickOn(lorryMainButton);
                sleep(1);
            }
        }
        finder.waitForImage(lorry);
        exit();
    }

    private void sendSleepingLorry() {
        enter();
        finder.leftClickOn(deployButton);
        chooseDestinationType();
        finder.leftClickOn(searchButton);
        sleep(3);
        leftClickAt(getWindowCenterPoint().shift(-20, 20));
        sleep(1);
        finder.leftClickOn(dispatchLorryButton);
        exit();
    }

    private void hideBottomLorryWindow() {
        finder.leftClickOn(lorryMainButton);
    }

    private void clickOnFactoryBuilding() {
        enter();
        do {
            leftClickAt(getWindowCenterPoint().shift(-76, -115));
            sleep(1);
        } while (!finder.isImageVisible(produceRoundButton));
        exit();
    }

    private void shiftProductsToRight() {
        enter();
        while (finder.isImageVisible(productLeftArrows)) {
            leftClickAt(finder.getCenterPointOf(productLeftArrows).shift(100, 50));
            sleep(0.2);
        }
        exit();
    }

    private void tapNextWorkshop(Image[] workshopImages) {
        enter((Object[]) workshopImages);
        finder.leftClickAnyImage(workshopImages);
        exit();
    }

    private void oneTapToRight() {
        enter();
        leftClickAt(finder.getCenterPointOf(productRightArrows).shift(-100, 50));
        sleep(0.5);
        exit();
    }

    private void orderProduct() {
        enter();
        if (finder.isImageVisible(produceGreenButton)) {
            while (finder.withColor(true).withTolerance(0.91).isImageVisible(whiteSlash)) {
                finder.leftClickOn(produceGreenButton);
                sleep(0.4);
            }
        }
        exit();
    }

    private void chooseDestinationType() {
        enter();
        Image[] destinations = {farm, mine, oilWell};
        Image choice;
        do {
            choice = destinations[random.nextInt(destinations.length)];
        } while (!finder.isImageVisible(choice));
        finder.leftClickOn(choice);
        exit();
    }

    private boolean canSeeSleepingLorry() {
        enter();
        return exit(finder.isAnyImageVisible(sleepingLorryIcon1, sleepingLorryIcon2));
    }

    private void showSleepingLorry() {
        enter();
        finder.mouseMoveTo(lorry);
        mouseDragDirection(lorryDragDirection, 300);
        lorryDragDirection =
              lorryDragDirection == Direction.LEFT
                    ? Direction.RIGHT
                    : Direction.LEFT;
        exit();
    }
}
