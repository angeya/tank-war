package top.angeya.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.angeya.enums.GameType;
import top.angeya.model.Bullet;
import top.angeya.model.Explosion;
import top.angeya.model.Home;
import top.angeya.model.tank.Bot;
import top.angeya.model.tank.Player1;
import top.angeya.model.tank.Tank;
import top.angeya.model.wall.Wall;
import top.angeya.util.MapUtil;
import top.angeya.util.ThreadPool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * @author: Angeya
 * @date: 2022/12/31 18:49
 **/
public class GamePanel extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(GamePanel.class);

    /**
     * 帧率
     */
    public static final int FPS = 20;

    private final MainFrame container;

    private final int level;

    private final GameType gameType;

    /**
     * 按键状态 <keyCode, 是否被按下>
     */
    private static final Map<Integer, Boolean> KEY_STATUS_MAP = new HashMap<>();

    private Tank player1;

    private Tank player2;

    private final List<Tank> botList = new LinkedList<>();

    private final Home home = new Home(367, 532);

    /**
     * 电脑坦克出生的3个横坐标位置
     */
    private final int[] botPositionXs = { 10, 367, 754 };

    /**
     * 上一次创建电脑坦克的时间
     */
    private long lastCreateBotTime = 0;

    private final Random random = new Random();

    private List<Wall> wallList;

    private BufferedImage mainImage;
    private Graphics2D graphics2D;

    private static final int MAX_BOT_NUMBER = 20;

    private final List<Explosion> explosionList =  new LinkedList<>();
    
    /**
     * 剩余未出场的电脑坦克数量
     */
    private int restBotNumber = 6;

    /**
     * 按键监听器
     */
    private final KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            Integer keyCode = e.getKeyCode();
            if (KEY_STATUS_MAP.containsKey(keyCode)) {
                KEY_STATUS_MAP.put(keyCode, Boolean.TRUE);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            Integer keyCode = e.getKeyCode();
            if (KEY_STATUS_MAP.containsKey(keyCode)) {
                KEY_STATUS_MAP.put(keyCode, Boolean.FALSE);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            Integer keyCode = e.getKeyCode();
            if (KEY_STATUS_MAP.containsKey(keyCode)) {
                KEY_STATUS_MAP.put(keyCode, Boolean.TRUE);
            }
        }
    };

    // 添加按键状态
    static {
        KEY_STATUS_MAP.put(KeyEvent.VK_UP, Boolean.FALSE);
        KEY_STATUS_MAP.put(KeyEvent.VK_DOWN, Boolean.FALSE);
        KEY_STATUS_MAP.put(KeyEvent.VK_LEFT, Boolean.FALSE);
        KEY_STATUS_MAP.put(KeyEvent.VK_RIGHT, Boolean.FALSE);
        KEY_STATUS_MAP.put(KeyEvent.VK_A, Boolean.FALSE);
    }

    /**
     * 游戏是否结束
     */
    private volatile boolean finish = false;

    public GamePanel(MainFrame container, int level, GameType gameType) {
        this.container = container;
        this.level = level;
        this.gameType = gameType;
        this.init();
        this.container.addKeyListener(this.keyListener);
        ThreadPool.getThreadPool().execute(() -> {
            while (!this.finish) {
                // 执行本类重绘方法
                repaint();
                try {
                    Thread.sleep(FPS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    };

    /**
     * 初始化
     */
    private void init() {
        this.player1 = new Player1(this);
        this.wallList = MapUtil.loadMapByLevel(this.level);
        this.wallList.addAll(MapUtil.getHomeWallList());
        this.mainImage = new BufferedImage(794, 572, BufferedImage.TYPE_INT_BGR);
        // 获取主图片绘图对象
        this.graphics2D = this.mainImage.createGraphics();
    }



    @Override
    public void paint(Graphics g) {
        graphics2D.setColor(Color.WHITE);
        // 填充一个覆盖整个图片的白色矩形
        graphics2D.fillRect(0, 0, mainImage.getWidth(), mainImage.getHeight());
        this.paintMap();
        this.playerMove();
        this.playerShot();
        this.removeDisposedBot();
        this.createBot();
        this.botMoveAndShot();
        this.paintTankAndBullet();
        this.paintExplosion();
        if (this.isGameFinish()) {
            this.container.removeKeyListener(keyListener);
            return;
        }
        // 将主图片绘制到面板上
        g.drawImage(mainImage, 0, 0, this);
    }

    /**
     * 玩家坦克根据按键状态进行移动
     */
    private void playerMove() {
        if (KEY_STATUS_MAP.getOrDefault(KeyEvent.VK_UP, Boolean.FALSE)) {
            this.player1.goUp();
        } else if (KEY_STATUS_MAP.getOrDefault(KeyEvent.VK_DOWN, Boolean.FALSE)) {
            this.player1.goDown();
        } else if (KEY_STATUS_MAP.getOrDefault(KeyEvent.VK_LEFT, Boolean.FALSE)) {
            this.player1.goLeft();
        } else if (KEY_STATUS_MAP.getOrDefault(KeyEvent.VK_RIGHT, Boolean.FALSE)) {
            this.player1.goRight();
        }
    }
    /**
     * 玩家坦克根据按键状态射击
     */
    private void playerShot() {
        if (KEY_STATUS_MAP.getOrDefault(KeyEvent.VK_A, Boolean.FALSE)) {
            this.player1.shot();
        }
    }
    /**
     * 游戏是否结束
     * @return 是否结束
     */
    private boolean isGameFinish() {
        boolean lose = !this.player1.isAlive() ||
                !this.player1.isAlive() && this.gameType == GameType.TWO_PLAYER && this.player2.isAlive();
        if (lose) {
            this.finish = true;
            this.switchLevel(false);
        }
        boolean win = this.restBotNumber == 0 && this.botList.isEmpty();
        if (win) {
            this.finish = true;
            this.switchLevel(true);
        }
        return lose && win;
    }

    /**
     * 移除已经被销毁的电脑坦克
     */
    private void removeDisposedBot() {
        Iterator<Tank> tankIterator = this.botList.iterator();
        while (tankIterator.hasNext()) {
            Tank tank = tankIterator.next();
            if (!tank.isAlive()) {
                tank.dispose();
                tankIterator.remove();
            }
        }
    }

    /**
     * 绘制爆炸
     */
    private void paintExplosion() {
        Iterator<Explosion> iterator = this.explosionList.iterator();
        while (iterator.hasNext()) {
            Explosion explosion = iterator.next();
            if (explosion.isAlive()) {
                this.graphics2D.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
            } else {
                iterator.remove();
            }
        }
    }

    /**
     * 电脑移动和设计
     */
    private void botMoveAndShot() {
        this.botList.forEach(bot -> {
            ((Bot)bot).move();
            ((Bot)bot).shot();
        });
    }



    /**
     * 绘制坦克和子弹
     */
    private void paintTankAndBullet() {
        List<Tank> tankList = this.getTankList();
        for (Tank tank : tankList) {
            // 绘制坦克
            graphics2D.drawImage(tank.getImage(), tank.getX(), tank.getY(), this.container);
            // 获取坦克的子弹集合并绘制，使用迭代器方式遍历，便于删除
            List<Bullet> bulletList = new LinkedList<>(tank.getBulletList());
            Iterator<Bullet> bulletIterator = bulletList.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (bullet.isAlive()) {
                    bullet.move();
                    graphics2D.drawImage(bullet.getImage(), bullet.getX(), bullet.getY(), this.container);
                } else {
                    bulletIterator.remove();
                }
            }
        }
    }

    /**
     * 绘制地图
     */
    private void paintMap() {
        this.graphics2D.drawImage(this.home.getImage(), this.home.getX(), this.home.getY(), this);
        this.wallList.forEach(wall ->
                this.graphics2D.drawImage(wall.getImage(), wall.getX(), wall.getY(), this.container));
    }

    /**
     * 添加电脑坦克，如果场上坦克未到达最大值，每4秒钟之后在三个出生位置随机选择其一，创建电脑坦克。
     */
    private void createBot() {
        long now = System.currentTimeMillis();
        // 如果没有电脑坦克，则立马创建3个
        if (this.restBotNumber != 0 && this.botList.isEmpty()) {
            this.botList.add(new Bot(this, botPositionXs[0], 1));
            this.botList.add(new Bot(this, botPositionXs[1], 1));
            this.botList.add(new Bot(this, botPositionXs[2], 1));
            this.restBotNumber = this.restBotNumber - 3;
            this.lastCreateBotTime = now;
            return;
        }
        // “当场上电脑小于场上最大数时” 并且 “剩余坦克数量大于0” 并且 “计时器记录已过去4秒钟”
        boolean canCreateBot = this.botList.size() < MAX_BOT_NUMBER && this.restBotNumber > 0 && now - lastCreateBotTime >= 4000;
        if (canCreateBot) {
            int index = random.nextInt(3);
            // 创建坦克随机出生区域
            Rectangle bornBoundary = new Rectangle(botPositionXs[index], 1, 35, 35);
            for (Tank bot : botList) {
                // 如果场上存在与随机位置重合并存活的坦克则先不创建
                if (bot.isAlive() && bot.hit(bornBoundary)) {
                    return;
                }
            }
            // 在随机位置创造电脑坦克
            this.botList.add(new Bot(this, botPositionXs[index], 1));
            this.restBotNumber--;
            this.lastCreateBotTime = now;
        }
    }

    public List<Wall> getWallList() {
        return this.wallList;
    }

    /**
     * 获取地图中所有的坦克，包括玩家和电脑
     * @return 坦克集合
     */
    public List<Tank> getTankList() {
        List<Tank> tankList = new LinkedList<>(this.botList);
        if (this.player1.isAlive()) {
            tankList.add(this.player1);
        }
        if (this.gameType == GameType.TWO_PLAYER && this.player2.isAlive()) {
            tankList.add(this.player2);
        }
        return tankList;
    }

    /**
     * 增加爆炸
     * @param explosion 爆炸对象
     */
    public void addExplosion(Explosion explosion) {
        this.explosionList.add(explosion);
    }

    /**
     * 切换关卡，延时一定时间，然后跳转到关卡，再进入游戏
     * @param isNext 是否下一个关卡
     */
    private void switchLevel(boolean isNext) {
        int nextLevel = isNext? this.level + 1: this.level;
        ThreadPool.getThreadPool().execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            container.setPanel(new LevelPanel(container, gameType, nextLevel));
        });
    }
}
