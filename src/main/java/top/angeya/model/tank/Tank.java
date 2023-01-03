package top.angeya.model.tank;

import top.angeya.enums.Direction;
import top.angeya.model.Bullet;
import top.angeya.model.Movable;
import top.angeya.model.VisibleObject;
import top.angeya.model.wall.GrassWall;
import top.angeya.model.wall.Wall;
import top.angeya.ui.GamePanel;
import top.angeya.util.ThreadPool;
import top.angeya.util.Tools;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Angeya
 * @date: 2022/12/31 21:03
 **/
public abstract class Tank extends VisibleObject implements Movable {

    protected final GamePanel gamePanel;

    protected Direction direction;

    private int speed = 3;

    private BufferedImage upImage;

    private BufferedImage downImage;

    private BufferedImage rightImage;

    private BufferedImage leftImage;

    /**
     * 坦克的子弹集合
     */
    private final List<Bullet> bulletList = new ArrayList<>();

    /**
     * 攻击冷却状态
     */
    protected boolean isShotCoolDown = true;

    /**
     *  攻击冷却时间，毫秒
     */
    private int shotCoolDownTime = 500;

    public Tank(GamePanel gamePanel, Direction direction,
                int x, int y, String imagePath,
                String upImagePath, String downImagePath,
                String leftImagePath, String rightImagePath) {
        super(x, y, imagePath);
        this.gamePanel = gamePanel;
        this.direction = direction;
        this.initImages(upImagePath, downImagePath, leftImagePath, rightImagePath);
    }

    public Tank(GamePanel gamePanel, Direction direction,
                int speed, int shotCoolDownTime,
                int x, int y, String imagePath,
                String upImagePath, String downImagePath,
                String leftImagePath, String rightImagePath) {
        super(x, y, imagePath);
        this.speed = speed;
        this.shotCoolDownTime = shotCoolDownTime;
        this.gamePanel = gamePanel;
        this.direction = direction;
        this.initImages(upImagePath, downImagePath, leftImagePath, rightImagePath);
    }

    private void initImages(String upImagePath, String downImagePath,
                            String leftImagePath, String rightImagePath) {
        this.upImage = Tools.loadImage(upImagePath);
        this.downImage = Tools.loadImage(downImagePath);
        this.leftImage = Tools.loadImage(leftImagePath);
        this.rightImage = Tools.loadImage(rightImagePath);
    }

    @Override
    public void move() {

    }

    @Override
    public void beyondBoundary() {
        // 如果坦克横坐标小于0，让坦克横坐标等于0
        if (x < 0) {
            x = 0;
        } else if (x > gamePanel.getWidth() - width) {
            // 如果坦克横坐标超出了最大范围，让坦克横坐标保持最大值
            x = gamePanel.getWidth() - width;
        }
        // 如果坦克纵坐标小于0，让坦克纵坐标等于0
        if (y < 0) {
            y = 0;
        } else if (y > gamePanel.getHeight() - height) {
            // 如果坦克纵坐标超出了最大范围，让坦克纵坐标保持最大值
            y = gamePanel.getHeight() - height;
        }
    }

    /**
     * 向左移动
     */
    public void goLeft() {
        // 如果移动之前的不是该方向，则更换图像
        if (this.direction != Direction.LEFT) {
            direction = Direction.LEFT;
            setImage(this.leftImage);
        }
        // 如果左移之后的位置不会撞到墙块和坦克
        if (!isHitWall(this.x - this.speed, this.y) && !isHitTank(this.x - this.speed, this.y)) {
            this.x -= this.speed;
            // 判断是否移动到面板的边界
            this.beyondBoundary();
        }
    }

    /**
     * 向右移动
     */
    public void goRight() {
        if (this.direction != Direction.RIGHT) {
            this.direction = Direction.RIGHT;
            setImage(rightImage);
        }
        if (!isHitWall(this.x + this.speed, this.y) && !isHitTank(this.x + this.speed, this.y)) {
            this.x += this.speed;
            this.beyondBoundary();
        }
    }

    /**
     * 向上移动
     */
    public void goUp() {
        if (direction != Direction.UP) {
            direction = Direction.UP;
            setImage(upImage);
        }
        if (!isHitWall(x, y - speed) && !isHitTank(x, y - speed)) {
            y -= speed;
            beyondBoundary();
        }
    }

    /**
     * 向下移动
     */
    public void goDown() {
        if (direction != Direction.DOWN) {
            direction = Direction.DOWN;
            setImage(downImage);
        }
        if (!isHitWall(x, y + speed) && !isHitTank(x, y + speed)) {
            y += speed;
            beyondBoundary();
        }
    }

    /**
     * 
     * @param x 
     * @param y 
     * @return
     */
    private boolean isHitWall(int x, int y) {
        // 创建坦克移动的下一个目标区域
        Rectangle next = new Rectangle(x, y, width, height);
        // 获取所有墙块
        List<Wall> walls = gamePanel.getWallList();
        for (Wall wall : walls) {
            boolean isHit = !(wall instanceof GrassWall) && wall.hit(next);
            if (isHit) {
                return true;
            }
        }
        return false;
    }

    /**
     * 射击
     */
    public void shot() {
        // 射击冷却后才能发射
        if (this.isShotCoolDown) {
            this.isShotCoolDown = false;
            Point point = getHeadPoint();
            // 在坦克头位置发射与坦克角度相同的子弹
            Bullet bullet = new Bullet(point.x - Bullet.LENGTH / 2, point.y - Bullet.LENGTH / 2,
                    direction, gamePanel, this);
            this.bulletList.add(bullet);
            // 射击冷却
            Runnable shotCdTask = () -> {
                try {
                    Thread.sleep(shotCoolDownTime);
                    this.isShotCoolDown = true;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };
            ThreadPool.getThreadPool().execute(shotCdTask);
        }
    }

    /**
     * 获取坦克头点，作为子弹发射的位置
     * @return 头点对象
     */
    private Point getHeadPoint() {
        // 创建点对象，作为头点
        Point p = new Point();
        switch (direction) {
            case UP:
                p.x = x + width / 2;
                p.y = y;
                break;
            case DOWN:
                p.x = x + width / 2;
                p.y = y + height;
                break;
            case RIGHT:
                p.x = x + width;
                p.y = y + height / 2;
                break;
            case LEFT:
                p.x = x;
                p.y = y + height / 2;
                break;
            default:
                p = null;
        }
        return p;
    }
    boolean isHitTank(int x, int y) {
        // 创建坦克移动后的目标区域
        Rectangle next = new Rectangle(x, y, width, height);
        List<Tank> tankList = this.gamePanel.getTankList();
        for (Tank tank : tankList) {
            // 如果此坦克与自身不是同一个对象
            if (!this.equals(tank)) {
                if (tank.hit(next)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Bullet> getBulletList() {
        return bulletList;
    }
}
