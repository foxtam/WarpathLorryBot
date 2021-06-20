package net.foxtam.warpathlorry.bot;


import net.foxtam.foxclicker.*;

import java.util.List;
import java.util.Random;

import static net.foxtam.foxclicker.GlobalLogger.enter;
import static net.foxtam.foxclicker.GlobalLogger.exit;

public class WarpathBot extends Bot implements Runnable {

    private final Finder finder = new Finder(4.0, 0.85, false);
    private final double pauseInMinutes;

    public WarpathBot(double pauseInMinutes, Runnable onStop, Runnable onPause) {
        super(KeyConfig.getDefault(), Window.getByTitle("NoxPlayer"), onStop, onPause);
        enter(pauseInMinutes);
        this.pauseInMinutes = pauseInMinutes;
        exit();
    }

    @Override
    protected void action() {
        enter();
        Lorries lorries = new Lorries();
        Factory factory = new Factory();

        //noinspection InfiniteLoopStatement
        while (true) {
            lorries.run();
            factory.run();
            sleep(pauseInMinutes * 60);
        }
    }

    class Lorries {
        final Image lorryMainButton = Image.loadFromResource("/images/lorry_main_button.png");
        final Image deployButton = Image.loadFromResource("/images/deploy_button.png");
        final Image lorry = Image.loadFromResource("/images/lorry.png");
        final Image farm = Image.loadFromResource("/images/farm.png");
        final Image mine = Image.loadFromResource("/images/mine.png");
        final Image oilWell = Image.loadFromResource("/images/oil_well.png");
        final Image searchButton = Image.loadFromResource("/images/search_button.png");
        final Image dispatchLorryButton = Image.loadFromResource("/images/dispatch_lorry_button.png");
        final Image recallLorryButton = Image.loadFromResource("/images/recall_lorry_button.png");
        final Image lvlOnRight = Image.loadFromResource("/images/lvl_on_right.png");
        final Image plusLvlButton = Image.loadFromResource("/images/plus_lvl_button.png");
        final Image minusLvlButton = Image.loadFromResource("/images/minus_lvl_button.png");
        final Image noDetectedNearby = Image.loadFromResource("/images/no_detected_nearby.png");
        private final Random random = new Random();

        public void run() {
            lorryBypass();
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
            setupMaxLevel();

            finder.leftClickOn(searchButton);
            while (finder.withTime(1).isImageVisible(noDetectedNearby)) {
                finder.leftClickOn(minusLvlButton);
                finder.leftClickOn(searchButton);
                sleep(1.5);
            }

            sleep(1);
            leftClickAt(getWindowCenterPoint().shift(-20, 20));
            finder.leftClickOn(dispatchLorryButton);
            exit();
        }

        private void hideBottomLorryWindow() {
            finder.leftClickOn(lorryMainButton);
        }

        private void chooseDestinationType() {
            enter();
            Image[] destinations = {farm, mine, oilWell};
            Image choice;
            do {
                choice = destinations[random.nextInt(destinations.length)];
            } while (!finder.withTime(0.2).isImageVisible(choice));
            finder.leftClickOn(choice);
            exit();
        }

        private void setupMaxLevel() {
            while (!finder.withTime(0.1).isImageVisible(lvlOnRight)) {
                finder.leftClickOn(plusLvlButton);
            }
        }
    }

    class Factory {

        final Image returnToBaseButton = Image.loadFromResource("/images/return_to_base_button.png");
        final Image produceRoundButton = Image.loadFromResource("/images/produce_round_button.png");
        final Image produceGreenButton = Image.loadFromResource("/images/produce_green_button.png");
        final Image productLeftArrows = Image.loadFromResource("/images/product_left_arrows.png");
        final Image productRightArrows = Image.loadFromResource("/images/product_right_arrows.png");
        final Image whiteSlash = Image.loadFromResource("/images/white_slash.png");
        final Image backToMainScreenButton = Image.loadFromResource("/images/back_to_main_screen_button.png");

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

        public void run() {
            factoryBypass();
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
                if (finder.withTime(0.1).isAnyImageVisible(workshop)) {
                    tapNextWorkshop(workshop);
                    oneTapToRight();
                    orderProduct();
                } else //noinspection BreakStatement
                    break;
            }

            finder.leftClickOn(backToMainScreenButton);
            exit();
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
            while (finder.withTime(0.5).isImageVisible(productLeftArrows)) {
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
                ScreenPoint productGreenButtonPoint = finder.getCenterPointOf(produceGreenButton);
                while (finder
                      .withColor(true)
                      .withTolerance(0.91)
                      .withTime(2)
                      .isImageVisible(whiteSlash)) {
                    for (int i = 0; i < 5; i++) {
                        leftClickAt(productGreenButtonPoint);
                        sleep(0.02);
                    }
                }
            }
            exit();
        }
    }
}
