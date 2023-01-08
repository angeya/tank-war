package top.angeya.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.angeya.enums.Direction;
import top.angeya.model.tank.Tank;
import top.angeya.model.wall.BrickWall;
import top.angeya.model.wall.Wall;
import top.angeya.ui.GamePanel;
import top.angeya.util.MapUtil;
import top.angeya.util.Music;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

/**
 * @author: Angeya
 * @date: 2023/1/1 17:30
 **/
public class Bullet extends VisibleObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapUtil.class);

    /**
     * 子弹边长
     */
    public static final int LENGTH = 8;

    private Direction direction;

    private int speed = 7;

    /**
     * 游戏窗口，用户获取墙、坦克等数据
     */
    private GamePanel gamePanel;

    /**
     * 发出子弹的坦克
     */
    Tank owner;

    /**
     * 子弹颜色.橙色
     */
    private final Color color = Color.ORANGE;

    public Bullet(int x, int y, Direction direction, GamePanel gamePanel, Tank owner) {
        super(x, y, LENGTH, LENGTH);
        this.direction = direction;
        this.gamePanel = gamePanel;
        this.owner = owner;
        init();// 初始化组件
    }

    /**
     * 初始化子弹样式
     */
    private void init() {
        Graphics g = image.getGraphics();
        // 使用白色绘图，绘制一个铺满整个图片的白色实心矩形
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, LENGTH, LENGTH);
        // 使用子弹颜色,绘制一个铺满整个图片的实心圆形
        g.setColor(this.color);
        g.fillOval(0, 0, LENGTH, LENGTH);
        // 使用黑色，给圆形绘制一个黑色的边框，防止绘出界，宽高减小1像素
        g.setColor(Color.BLACK);
        g.drawOval(0, 0, LENGTH - 1, LENGTH - 1);
    }

    /**
     * 子弹移动
     */
    public void move() {
        // 判断移动方向
        switch (this.direction) {
            case UP:
                y -= speed;
                break;
            case DOWN:
                y += speed;
                break;
            case LEFT:
                x -= speed;
                break;
            case RIGHT:
                x += speed;
                break;
            default:
                LOGGER.error("Invalid direction: {}", this.direction);
        }
        this.beyondBoundary();
        this.checkHitWall();
        this.checkHitTank();
    }

    /**
     * 检查是否击中坦克
     */
    private void checkHitTank() {
        this.gamePanel.getTankList().forEach(tank -> {
            boolean isHit = this.getBoundary().intersects(tank.getBoundary());
            boolean isNotTeammate = !this.owner.getClass().equals(tank.getClass());
            // 没有队友伤害
            if (isHit && isNotTeammate) {
                tank.dispose();
                Explosion explosion = new Explosion(tank.getX(), tank.getY());
                this.gamePanel.addExplosion(explosion);
                Music.playExplosionMusic();
            }
        });
    }

    /**
     * 是否击中墙
     */
    private void checkHitWall() {
        Rectangle bulletBoundary = this.getBoundary();
        List<Wall> wallList = this.gamePanel.getWallList();
        Iterator<Wall> wallIterator = wallList.iterator();
        while (wallIterator.hasNext()) {
            Wall wall = wallIterator.next();
            boolean hit = bulletBoundary.intersects(wall.getBoundary());
            if (hit) {
                this.dispose();
                if (wall instanceof BrickWall) {
                    wallIterator.remove();
                }
                return;
            }
        }
    }

    /**
     * 移动出面板边界时销毁子弹
     */
    private void beyondBoundary() {
        // 如果子弹坐标离开游戏面板
        if (x < 0 || x > gamePanel.getWidth() - getWidth() || y < 0 || y > gamePanel.getHeight() - getHeight()) {
            this.dispose();
        }
    }

}
