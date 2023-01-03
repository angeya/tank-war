package top.angeya.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author: Angeya
 * @date: 2022/12/31 17:35
 **/
public abstract class VisibleObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisibleObject.class);

    protected int x;

    protected int y;

    protected int width;

    protected int height;

    protected BufferedImage image;

    /**
     * 是否存活（存在）
     */
    protected boolean isAlive = true;

    /**
     * 给定宽高，图像通过绘画得到
     */
    public VisibleObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        // 实例化图片
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
    }

    /**
     * 通过路径加载图像，自动计算宽高
     */
    public VisibleObject(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            LOGGER.error("Load image failed, path is {}", imagePath);
            throw new RuntimeException(e);
        }
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    /**
     * 判断是否发生碰撞
     * @param r - 目标边界
     * @return 如果两者相交，则返回true，否则返回false
     */
    public boolean hit(Rectangle r) {
        // 如果目标为空
        if (r == null) {
            return false;
        }
        // 返回两者的边界对象是否相交
        return getBoundary().intersects(r);
    }

    /**
     * 获取边界对象
     */
    public Rectangle getBoundary() {
        // 创建一个坐标在(x,y)位置，宽高为(width, height)的矩形边界对象并返回
        return new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public synchronized void dispose() {
        this.isAlive = false;
    }
}
