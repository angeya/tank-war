package top.angeya.ui;

import top.angeya.constant.Const;
import top.angeya.enums.GameType;
import top.angeya.util.ThreadPool;

import javax.swing.*;
import java.awt.*;

/**
 * @author: Angeya
 * @date: 2022/12/31 16:21
 **/
public class LevelPanel extends JPanel {

    private final MainFrame container;

    private final GameType gameType;

    private final int level;

    private String levelTip;

    private String readyTip = "";

    public LevelPanel(MainFrame container, GameType gameType, int level) {
        this.container = container;
        this.gameType = gameType;
        this.level = level;
        this.blinkTip();
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        // 填充一个覆盖整个面板的白色矩形
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(new Font(Const.FONT_NAME, Font.BOLD, 50));
        // 填充一个覆盖整个面板的白色矩形
        g.setColor(Color.BLACK);
        g.drawString(levelTip, 260, 300);
        g.setColor(Color.RED);
        // 绘制准备提示
        g.drawString(readyTip, 270, 400);
    }

    /**
     * 闪烁关卡提示
     */
    private void blinkTip() {
        // 初始化后直接在主线程里闪烁会显示不正常
        Runnable task = () -> {
            for (int i = 0; i < 1; i++) {
                // 如果循环变量是偶数
                if (i % 2 == 0) {
                    this.levelTip = Const.LEVEL + level;
                } else {
                    this.levelTip = "123";
                }
                if (i == 4) {
                    this.readyTip = Const.READY_TIP;
                }
                repaint();// 重绘组件
                try {
                    // 休眠0.5秒
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.openGamePanel();
        };
        ThreadPool.getThreadPool().execute(task);
    }

    /**
     * 跳转游戏面板
     */
    private void openGamePanel() {
        // 主窗体跳转到此关卡游戏面板
        this.container.setPanel(new GamePanel(this.container, level, this.gameType));
    }
}
