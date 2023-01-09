package top.angeya.model.tank;

import top.angeya.constant.ImagePaths;
import top.angeya.enums.Direction;
import top.angeya.ui.GamePanel;

import java.util.Random;

/**
 * @author: Angeya
 * @date: 2022/12/31 21:18
 **/
public class Bot extends Tank{

    private final Random random = new Random();

    private static final int FRESH_TIME = GamePanel.FPS;

    private int moveTime = 0;

    public Bot(GamePanel gamePanel, int x, int y) {
        super(gamePanel, Direction.DOWN, x, y, ImagePaths.BOT_DOWN,
                ImagePaths.BOT_UP, ImagePaths.BOT_DOWN,
                ImagePaths.BOT_LEFT, ImagePaths.BOT_RIGHT);
    }

    @Override
    public void move() {
        Direction direction = this.direction;
        // 如果移动计时器记录超过3秒，随机调整移动方向
        if (moveTime >= 100 + this.random.nextInt(3000)) {
            direction = randomDirection();
            moveTime = 0;
        } else {
            // 计时器按照刷新时间递增
            moveTime += FRESH_TIME;
        }

        // 判断移动方向
        switch (direction) {
            case UP:
                this.goUp();
                break;
            case DOWN:
                this.goDown();
                break;
            case LEFT:
                this.goLeft();
                break;
            case RIGHT:
                this.goRight();
                break;
            default:

        }
    }

    /**
     * 获取随机方向
     * @return 方向
     */
    private Direction randomDirection() {
        // 获取随机数，范围在0-3
        int randomNum = random.nextInt(4);
        switch (randomNum) {
            case 0:
                return Direction.UP;
            case 1:
                return Direction.RIGHT;
            case 2:
                return Direction.LEFT;
            default:
                return Direction.DOWN;
        }
    }

    /**
     * 重写攻击方法，每次攻击只有4%概率会触发父类攻击方法
     * 可以修改此方法使得游戏难度更大
     */
    @Override
    public void shot() {
        // 如果攻击冷却时间结束则进行攻击
        if (!this.isShotCoolDown) {
            return;
        }
        // 创建随机数，范围在0-99，如果随机数小于4
        int randomNum = random.nextInt(100);
        if (randomNum < 4) {
            super.shot();
        }
    }
}
