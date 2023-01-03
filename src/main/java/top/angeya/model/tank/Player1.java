package top.angeya.model.tank;

import top.angeya.constant.ImagePaths;
import top.angeya.enums.Direction;
import top.angeya.ui.GamePanel;

/**
 * @author: Angeya
 * @date: 2022/12/31 21:18
 **/
public class Player1 extends Tank{

    private static final int ORIGINAL_X = 278;

    private static final int ORIGINAL_Y = 537;

    private static final int SPEED = 5;

    private static final int SHOT_COOL_DOWN_TIME = 100;

    public Player1(GamePanel gamePanel) {
        super(gamePanel, Direction.UP,
                SPEED, SHOT_COOL_DOWN_TIME,
                ORIGINAL_X, ORIGINAL_Y, ImagePaths.PLAYER1_UP,
                ImagePaths.PLAYER1_UP, ImagePaths.PLAYER1_DOWN,
                ImagePaths.PLAYER1_LEFT, ImagePaths.PLAYER1_RIGHT);
    }
}
