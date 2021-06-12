package net.foxtam.warpathlorry.bot;


import net.foxtam.foxclicker.*;

import java.util.List;
import java.util.Random;

public class WarpathBot extends Bot {

    final Image lorryMainButton = Image.loadFromResource("/images/lorry_main_button.png");
    final Image sendLorryButton = Image.loadFromResource("/images/send_lorry_button.png");
    final Image recallLorryButton = Image.loadFromResource("/images/recall_lorry_button.png");
    final Image lorry = Image.loadFromResource("/images/lorry.png");
    final Image sleepingLorryIcon = Image.loadFromResource("/images/sleeping_lorry_icon.png");
    final Image farm = Image.loadFromResource("/images/factory_building.png");
    final Image mine = Image.loadFromResource("/images/mine.png");
    final Image oilWell = Image.loadFromResource("/images/oil_well.png");
    final Image searchButton = Image.loadFromResource("/images/search_button.png");
    final Image roundSendLorryButton = Image.loadFromResource("/images/round_send_lorry_button.png");
    final Image returnToBaseButton = Image.loadFromResource("/images/return_to_base_button.png");
    final Image factoryBuilding = Image.loadFromResource("/images/factory_building.png");
    final Image productionRoundButton = Image.loadFromResource("/images/production_round_button.png");
    final Image inStock = Image.loadFromResource("/images/in_stock.png");
    final Image productionGreenButton = Image.loadFromResource("/images/production_green_button.png");
    final Image productLeftArrows = Image.loadFromResource("/images/product_left_arrows.png");
    final Image productRightArrows = Image.loadFromResource("/images/product_right_arrows.png");
    final Image redSlash = Image.loadFromResource("/images/red_slash.png");
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


    private final Random random = new Random();
    private final Finder finder = new Finder(4.0, 0.88, false);
    private final double pauseInMinutes;
    private Direction lorryDragDirection = Direction.LEFT;

    public WarpathBot(double pauseInMinutes) {
        super(KeyConfig.getDefault(), Window.getByTitle("NoxPlayer"));
        this.pauseInMinutes = pauseInMinutes;
    }

    @Override
    protected void action() {
        //noinspection InfiniteLoopStatement
        while (true) {
            lorryBypass();
            factoryBypass();
            sleep(pauseInMinutes * 60);
        }
    }

    private void lorryBypass() {
        while (true) {
            openBottomLorryWindow();
            if (!canSeeSleepingLorry()) showSleepingLorry();
            if (!canSeeSleepingLorry()) {
                finder.leftClickOn(lorryMainButton);
                return;
            }
            sendSleepingLorry();
        }
    }

    private void factoryBypass() {
        finder.leftClickOn(returnToBaseButton);
        do {
            finder.leftClickOn(factoryBuilding);
        } while (!finder.isImageVisible(productionRoundButton));
        finder.leftClickOn(productionRoundButton);
        finder.waitForImage(productionGreenButton);

        shiftProductsToRight();
        for (Image[] workshop : workshops) {
            tapNextWorkshop(workshop);
            oneTapToRight();
            orderProduct();
        }

        finder.leftClickOn(backToMainScreenButton);
    }

    private void openBottomLorryWindow() {
        for (int i = 0; i < 2; i++) {
            if (!finder.isImageVisible(lorry)) {
                finder.leftClickOn(lorryMainButton);
                sleep(1);
            }
        }
        finder.waitForImage(lorry);
    }

    private boolean canSeeSleepingLorry() {
        return finder.isImageVisible(sleepingLorryIcon);
    }

    private void showSleepingLorry() {
        finder.mouseMoveTo(lorry);
        mouseDragDirection(lorryDragDirection, 300);
        lorryDragDirection =
            lorryDragDirection == Direction.LEFT
                ? Direction.RIGHT
                : Direction.LEFT;
    }

    private void sendSleepingLorry() {
        finder.leftClickOn(sleepingLorryIcon);
        finder.leftClickOn(sendLorryButton);
        chooseDestinationType();
        finder.leftClickOn(searchButton);
        sleep(2);
        leftClickAt(getWindowCenterPoint());
        finder.leftClickOn(roundSendLorryButton);
    }

    private void shiftProductsToRight() {
        while (finder.isImageVisible(productLeftArrows)) {
            leftClickAt(finder.getCenterPointOf(productLeftArrows).shift(120, 70));
            sleep(0.2);
        }
    }

    private void tapNextWorkshop(Image[] workshopImages) {
        finder.leftClickAnyImage(workshopImages);
    }

    private void oneTapToRight() {
        leftClickAt(finder.getCenterPointOf(productRightArrows).shift(-120, 70));
        sleep(0.5);
    }

    private void orderProduct() {
        while (finder.withColor(true).isImageVisible(whiteSlash)) {
            finder.leftClickOn(productionGreenButton);
        }
    }

    private void chooseDestinationType() {
        Image[] destinations = {farm, mine, oilWell};
        Image choice;
        do {
            choice = destinations[random.nextInt(destinations.length)];
        } while (!finder.isImageVisible(choice));
        finder.leftClickOn(choice);
    }
}
