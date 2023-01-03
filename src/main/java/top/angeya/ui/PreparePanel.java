package top.angeya.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.angeya.constant.Const;
import top.angeya.constant.ImagePaths;
import top.angeya.enums.GameType;
import top.angeya.util.Tools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

/**
 * @author: Angeya
 * @date: 2022/12/31 14:51
 **/
public class PreparePanel extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreparePanel.class);
    /**
     * 父容器，用于设置按键监听器、切换面板等
     */
    private MainFrame container;

    /**
     * 游戏类型
     */
    private GameType gameType;

    /**
     * 背景图
     */
    private Image backgroundImage;

    /**
     * 坦克图
     */
    private Image tankImage;

    /**
     * 游戏选项列表
     */
    private final GameType[] gameOptions = {GameType.ONE_PLAYER, GameType.TWO_PLAYER};

    /**
     * 选项下标
     */
    private int optionIndex = 0;

    /**
     * 按键事件监听器
     */
    private final KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            switch (code) {
                case KeyEvent.VK_UP:
                    optionIndex =
                            Tools.getNextIndexInLoopList(optionIndex, gameOptions.length, true);
                    repaint();
                    break;
                case KeyEvent.VK_DOWN:
                    optionIndex =
                            Tools.getNextIndexInLoopList(optionIndex, gameOptions.length, false);
                    repaint();
                    break;
                case KeyEvent.VK_ENTER:
                    gameType = gameOptions[optionIndex];
                    openLevelPanel();
                    break;
                default:
                    LOGGER.warn("Key code is {}", code);
            }
        }
    };


    public PreparePanel(MainFrame container) {
        this.container = container;
        this.container.addKeyListener(keyListener);
        try {
            this.backgroundImage = ImageIO.read(new File(ImagePaths.PREPARE_BACKGROUND));
            this.tankImage = ImageIO.read(new File(ImagePaths.PLAYER1_RIGHT));
        } catch (IOException e) {
            LOGGER.error("Read images failed, {}", e);
        }
    }

    /**
     * 重写paint方法，实现面板内容绘制
     * @param g  the <code>Graphics</code> context in which to paint
     */
    @Override
    public void paint(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        g.setFont(new Font(Const.FONT_NAME, Font.BOLD, 35));
        g.setColor(Color.WHITE);
        for (GameType gameType : this.gameOptions) {
            g.drawString(gameType.getDesc(), 350, gameType.getPosition());
        }
        g.drawImage(this.tankImage, 280, this.gameOptions[this.optionIndex].getPosition() - 30, this);
    }

    /**
     * 打开关卡面板
     */
    private void openLevelPanel() {
        this.container.removeKeyListener(keyListener);
        this.container.setPanel(new LevelPanel(this.container, this.gameType, 1));
    }

}
