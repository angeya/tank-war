package top.angeya.ui;

import top.angeya.constant.Const;

import javax.swing.*;
import java.awt.*;

/**
 * @author: Angeya
 * @date: 2022/12/31 14:17
 **/
public class MainFrame extends JFrame {

    public MainFrame() {
        this.initWindow();
    }

    private void initWindow() {
        // 设置标题，尺寸，不可缩放
        this.setTitle(Const.GAME_TITLE);
        this.setSize(Const.MAIN_WINDOW_WIDTH, Const.MAIN_WINDOW_HEIGHT);
        this.setResizable(false);

        // 获取屏幕尺寸
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenDimension = toolkit.getScreenSize();

        // 设置居中位置，退出事件
        this.setLocation((screenDimension.width - this.getWidth()) / 2, (screenDimension.height - this.getHeight()) / 2);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//        this.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                int code = JOptionPane.showConfirmDialog(MainFrame.this, Const.WANT_TO_EXIT, Const.TIP,
//                        JOptionPane.YES_NO_CANCEL_OPTION);
//                if (code == JOptionPane.YES_OPTION) {
//                    System.exit(0);
//                }
//            }
//        });
        this.add(new PreparePanel(this));
    }

    /**
     * 更换主容器中的面板
     * @param panel
     * - 更换的面板
     */
    public void setPanel(JPanel panel) {
        // 获取主容器对象，删除容器中所有组件，添加新面板
        Container container = this.getContentPane();
        container.removeAll();
        container.add(panel);
        // 容器重新验证所有组件
        container.validate();
    }
}
