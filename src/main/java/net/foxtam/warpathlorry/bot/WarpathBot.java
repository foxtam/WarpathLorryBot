package net.foxtam.warpathlorry.bot;


import net.foxtam.foxclicker.*;
import net.foxtam.warpathlorry.bot.exceptions.AlreadyLoggedReset;
import net.foxtam.warpathlorry.bot.exceptions.ChooseDestinationException;

import java.util.List;
import java.util.Random;

import static net.foxtam.foxclicker.GlobalLogger.*;
import static net.foxtam.warpathlorry.bot.Images.*;

public class WarpathBot extends Bot implements Runnable {

    private static final String windowTitle = "NoxPlayer";
    ;
    private static final double defaultTimeLimit = 4;
    private static final double defaultTolerance = 0.85;
    private static final boolean defaultInColor = false;

    private final Frame noCheckFrame = getNoCheckFrame();
    private final Frame frame;

    private final double bypassPauseInMinutes;
    private final double alreadyLoggedPauseInMinutes;

    public WarpathBot(double bypassPauseInMinutes, double alreadyLoggedPauseInMinutes, Runnable onStop, Runnable onPause) {
        super(KeyConfig.getDefault(), onStop, onPause);
        enter(bypassPauseInMinutes);
        this.alreadyLoggedPauseInMinutes = alreadyLoggedPauseInMinutes;
        this.bypassPauseInMinutes = bypassPauseInMinutes;
        this.frame = getCheckFrame();
        exit();
    }

    private Frame getCheckFrame() {
        CheckScreen screen = CheckScreen.of(getChecks(), defaultTolerance, defaultInColor);
        return noCheckFrame.withWindow(noCheckFrame.getWindow().withScreen(screen));
    }

    private List<Pair<Image, Runnable>> getChecks() {
        return List.of(
                Pair.of(alreadyLogged, this::alreadyLoggedProcessing)
        );
    }

    private void alreadyLoggedProcessing() {
        sleep(alreadyLoggedPauseInMinutes * 60);
        noCheckFrame.leftClickOn(alreadyLoggedOkButton);
        frame.withTimeLimit(180).waitForImage(lorryMainButton);
        throw new AlreadyLoggedReset();
    }

    private Frame getNoCheckFrame() {
        return createFrame(
                Window.getByTitle(windowTitle, LoggedScreen.getInstance()),
                defaultTimeLimit,
                defaultTolerance,
                defaultInColor);
    }

    @Override
    protected void action() {
        enter();
        Lorries lorries = new Lorries();
        Factory factory = new Factory();

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                lorries.run();
                factory.run();
                sleep(bypassPauseInMinutes * 60);
            } catch (AlreadyLoggedReset ignore) {
            }
        }
    }

    class Lorries {

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
            Image button = frame.waitForAnyImage(deployButton, recallLorryButton);
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
                if (!frame.withTimeLimit(0.5).isImageVisible(lorry)) {
                    frame.leftClickOn(lorryMainButton);
                    sleep(0.5);
                }
            }
            frame.waitForImage(lorry);
            exit();
        }

        private void sendLorry(Runnable levelChooser) throws ChooseDestinationException {
            enter();
            frame.leftClickOn(deployButton);
            chooseDestinationType();
            levelChooser.run();
            frame.leftClickOn(searchButton);
            lowerLevelIfNoDetect();
            sleep(1);
            clickOnDestinationTown();
            dispatchSure();
            exit();
        }

        private void dispatchSure() throws ChooseDestinationException {
            if (frame.withTimeLimit(1).isImageVisible(dispatchLorryButton)) {
                frame.leftClickOn(dispatchLorryButton);
            } else {
                sendLorryOrStopDeploy(this::lowerLevel);
            }
        }

        private void clickOnDestinationTown() {
            frame.leftClickAt(frame.getCenterPoint().shift(-20, 20));
        }

        private void lowerLevelIfNoDetect() {
            while (frame.withTimeLimit(1).isImageVisible(noDetectedNearby)) {
                sleep(1.5);
                frame.leftClickOn(minusLvlButton);
                frame.leftClickOn(searchButton);
            }
        }

        private void hideBottomLorryWindow() {
            frame.leftClickOn(lorryMainButton);
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
            } while (!frame.withTimeLimit(0.2).isImageVisible(choice));
            frame.leftClickOn(choice);
            exit();
        }

        private void setupMaxLevel() {
            while (!frame.withTimeLimit(0.1).isImageVisible(lvlOnRight)) {
                frame.leftClickOn(plusLvlButton);
            }
        }

        private void lowerLevel() {
            frame.leftClickOn(minusLvlButton);
        }
    }

    class Factory {

        public void run() {
            factoryBypass();
        }

        private void factoryBypass() {
            enter();
            frame.leftClickOn(returnToBaseButton);
            sleep(2.0);
            clickOnFactoryBuilding();
            frame.leftClickOn(produceRoundButton);
            frame.waitForAnyImage(workshops.get(0));

            shiftProductsToRight();
            for (Image[] workshop : workshops) {
                if (frame.withTimeLimit(0.1).isAnyImageVisible(workshop)) {
                    tapNextWorkshop(workshop);
                    oneTapToRight();
                    orderProduct();
                } else //noinspection BreakStatement
                    break;
            }

            frame.leftClickOn(backToMainScreenButton);
            exit();
        }

        private void clickOnFactoryBuilding() {
            enter();
            do {
                frame.leftClickAt(frame.getCenterPoint().shift(-76, -115));
                sleep(1);
            } while (!frame.isImageVisible(produceRoundButton));
            exit();
        }

        private void shiftProductsToRight() {
            enter();
            while (frame.withTimeLimit(0.5).isImageVisible(productLeftArrows)) {
                frame.leftClickAt(frame.getCenterPointOf(productLeftArrows).shift(100, 50));
                sleep(0.2);
            }
            exit();
        }

        private void tapNextWorkshop(Image[] workshopImages) {
            enter((Object[]) workshopImages);
            frame.leftClickAnyImage(workshopImages);
            exit();
        }

        private void oneTapToRight() {
            enter();
            frame.leftClickAt(frame.getCenterPointOf(productRightArrows).shift(-100, 50));
            sleep(0.5);
            exit();
        }

        private void orderProduct() {
            enter();
            if (frame.isImageVisible(produceGreenButton)) {
                ScreenPoint productGreenButtonPoint = frame.getCenterPointOf(produceGreenButton);
                while (frame
                        .withInColor(true)
                        .withTolerance(0.91)
                        .withTimeLimit(2)
                        .isImageVisible(whiteSlash)) {
                    for (int i = 0; i < 5; i++) {
                        frame.leftClickAt(productGreenButtonPoint);
                        sleep(0.02);
                    }
                }
            }
            exit();
        }
    }
}
