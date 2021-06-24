package net.foxtam.warpathlorry.bot;


import net.foxtam.foxclicker.*;
import net.foxtam.warpathlorry.bot.exceptions.ChooseDestinationException;

import java.util.List;
import java.util.Random;

import static net.foxtam.foxclicker.GlobalLogger.*;

public class WarpathBot extends Bot implements Runnable {

    private static final int defaultNoxResolutionHeight = 900;
    private static final int noxVerticalExcess = 34;
    private final Finder finder = new Finder(4.0, 0.85, false);
    private final double pauseInMinutes;
    private final ScaleImageLoader loader;

    public WarpathBot(double pauseInMinutes, Runnable onStop, Runnable onPause) {
        super(KeyConfig.getDefault(), Window.getByTitle("NoxPlayer"), onStop, onPause);
        enter(pauseInMinutes);
        this.pauseInMinutes = pauseInMinutes;
        int resolutionHeight = getWidowRectangle().height - noxVerticalExcess;
        double scale = ((double) resolutionHeight) / defaultNoxResolutionHeight;
        this.loader = new ScaleImageLoader(1.0);
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
        final Image lorryMainButton = loader.loadFromResource("/images/px900/lorry_main_button.png");
        final Image deployButton = loader.loadFromResource("/images/px900/deploy_button.png");
        final Image lorry = loader.loadFromResource("/images/px900/lorry.png");
        final Image farm = loader.loadFromResource("/images/px900/farm.png");
        final Image mine = loader.loadFromResource("/images/px900/mine.png");
        final Image oilWell = loader.loadFromResource("/images/px900/oil_well.png");
        final Image searchButton = loader.loadFromResource("/images/px900/search_button.png");
        final Image dispatchLorryButton = loader.loadFromResource("/images/px900/dispatch_lorry_button.png");
        final Image recallLorryButton = loader.loadFromResource("/images/px900/recall_lorry_button.png");
        final Image lvlOnRight = loader.loadFromResource("/images/px900/lvl_on_right.png");
        final Image plusLvlButton = loader.loadFromResource("/images/px900/plus_lvl_button.png");
        final Image minusLvlButton = loader.loadFromResource("/images/px900/minus_lvl_button.png");
        final Image noDetectedNearby = loader.loadFromResource("/images/px900/no_detected_nearby.png");
        private final Random random = new Random();

        public void run() {
            lorryBypass();
        }

        private void lorryBypass() {
            enter();
            while (true) {
                try {
                    if (sendLorryOrStopDeploy(this::setupMaxLevel)) {
                        exit();
                        return;
                    }
                } catch (ChooseDestinationException ignore) {
                }
            }
        }

        private boolean sendLorryOrStopDeploy(Runnable levelChooser) throws ChooseDestinationException {
            enter();
            openBottomLorryWindow();
            Image button = finder.waitForAnyImage(deployButton, recallLorryButton);
            if (button == deployButton) {
                sendLorry(levelChooser);
            } else if (button == recallLorryButton) {
                hideBottomLorryWindow();
                return exit(true);
            }
            return exit(false);
        }

        private void openBottomLorryWindow() {
            enter();
            for (int i = 0; i < 2; i++) {
                if (!finder.withTime(0.5).isImageVisible(lorry)) {
                    finder.leftClickOn(lorryMainButton);
                    sleep(0.5);
                }
            }
            finder.waitForImage(lorry);
            exit();
        }

        private void sendLorry(Runnable levelChooser) throws ChooseDestinationException {
            enter();
            finder.leftClickOn(deployButton);
            chooseDestinationType();
            levelChooser.run();

            finder.leftClickOn(searchButton);
            lowerLevelIfNoDetect();

            clickOnDestinationTown();
            dispatchSure();
            exit();
        }

        private void dispatchSure() throws ChooseDestinationException {
            if (finder.withTime(1).isImageVisible(dispatchLorryButton)) {
                finder.leftClickOn(dispatchLorryButton);
            } else {
                sendLorryOrStopDeploy(this::lowerLevel);
            }
        }

        private void clickOnDestinationTown() {
            sleep(1.5);
            leftClickAt(getWindowCenterPoint().shift(-20, 20));
        }

        private void lowerLevelIfNoDetect() {
            while (finder.withTime(1).isImageVisible(noDetectedNearby)) {
                finder.leftClickOn(minusLvlButton);
                finder.leftClickOn(searchButton);
                sleep(1.5);
            }
        }

        private void hideBottomLorryWindow() {
            finder.leftClickOn(lorryMainButton);
        }

        private void chooseDestinationType() throws ChooseDestinationException {
            enter();
            Image[] destinations = {farm, mine, oilWell};
            Image choice;
            int counter = 0;
            do {
                choice = destinations[random.nextInt(destinations.length)];
                counter++;
                if (counter > 20) {
                    throw exception(new ChooseDestinationException());
                }
            } while (!finder.withTime(0.2).isImageVisible(choice));
            finder.leftClickOn(choice);
            exit();
        }

        private void setupMaxLevel() {
            while (!finder.withTime(0.1).isImageVisible(lvlOnRight)) {
                finder.leftClickOn(plusLvlButton);
            }
        }

        private void lowerLevel() {
            finder.leftClickOn(minusLvlButton);
        }
    }

    class Factory {

        final Image returnToBaseButton = loader.loadFromResource("/images/px900/return_to_base_button.png");
        final Image produceRoundButton = loader.loadFromResource("/images/px900/produce_round_button.png");
        final Image produceGreenButton = loader.loadFromResource("/images/px900/produce_green_button.png");
        final Image productLeftArrows = loader.loadFromResource("/images/px900/product_left_arrows.png");
        final Image productRightArrows = loader.loadFromResource("/images/px900/product_right_arrows.png");
        final Image whiteSlash = loader.loadFromResource("/images/px900/white_slash.png");
        final Image backToMainScreenButton = loader.loadFromResource("/images/px900/back_to_main_screen_button.png");

        final List<Image[]> workshops =
                List.of(
                        new Image[]{
                                loader.loadFromResource("/images/px900/workshop_1_1.png"),
                                loader.loadFromResource("/images/px900/workshop_1_2.png")
                        },
                        new Image[]{
                                loader.loadFromResource("/images/px900/workshop_2_1.png"),
                                loader.loadFromResource("/images/px900/workshop_2_2.png"),
                        },
                        new Image[]{
                                loader.loadFromResource("/images/px900/workshop_3_1.png"),
                                loader.loadFromResource("/images/px900/workshop_3_2.png")
                        },
                        new Image[]{
                                loader.loadFromResource("/images/px900/workshop_4_1.png"),
                                loader.loadFromResource("/images/px900/workshop_4_2.png")
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
