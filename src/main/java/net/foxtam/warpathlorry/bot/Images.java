package net.foxtam.warpathlorry.bot;

import net.foxtam.foxclicker.Image;
import net.foxtam.foxclicker.ScaleImageLoader;

import java.util.List;

public class Images {
    private static final ScaleImageLoader loader = new ScaleImageLoader(1.0);
    public static final Image lorryMainButton = loader.loadFromResource("/images/px900/lorry_main_button.png");
    public static final Image alreadyLogged = loader.loadFromResource("/images/px900/already_logged.png");
    public static final Image alreadyLoggedOkButton = loader.loadFromResource("/images/px900/already_logged_ok_button.png");
    public static final Image deployButton = loader.loadFromResource("/images/px900/deploy_button.png");
    public static final Image lorry = loader.loadFromResource("/images/px900/lorry.png");
    public static final Image farm = loader.loadFromResource("/images/px900/farm.png");
    public static final Image mine = loader.loadFromResource("/images/px900/mine.png");
    public static final Image oilWell = loader.loadFromResource("/images/px900/oil_well.png");
    public static final Image searchButton = loader.loadFromResource("/images/px900/search_button.png");
    public static final Image dispatchLorryButton = loader.loadFromResource("/images/px900/dispatch_lorry_button.png");
    public static final Image recallLorryButton = loader.loadFromResource("/images/px900/recall_lorry_button.png");
    public static final Image lvlOnRight = loader.loadFromResource("/images/px900/lvl_on_right.png");
    public static final Image plusLvlButton = loader.loadFromResource("/images/px900/plus_lvl_button.png");
    public static final Image minusLvlButton = loader.loadFromResource("/images/px900/minus_lvl_button.png");
    public static final Image noDetectedNearby = loader.loadFromResource("/images/px900/no_detected_nearby.png");
    public static final Image returnToBaseButton = loader.loadFromResource("/images/px900/return_to_base_button.png");
    public static final Image produceRoundButton = loader.loadFromResource("/images/px900/produce_round_button.png");
    public static final Image produceGreenButton = loader.loadFromResource("/images/px900/produce_green_button.png");
    public static final Image productLeftArrows = loader.loadFromResource("/images/px900/product_left_arrows.png");
    public static final Image productRightArrows = loader.loadFromResource("/images/px900/product_right_arrows.png");
    public static final Image whiteSlash = loader.loadFromResource("/images/px900/white_slash.png");
    public static final Image backToMainScreenButton = loader.loadFromResource("/images/px900/back_to_main_screen_button.png");

    public static final List<Image[]> workshops =
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
}
