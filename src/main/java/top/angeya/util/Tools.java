package top.angeya.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author: Angeya
 * @date: 2022/12/31 15:29
 **/
public class Tools {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tools.class);

    private Tools() {
    }

    /**
     * 获取循环列表的下一个下标
     * @param current 当前下标
     * @param listSize 列表大小
     * @param isForward 向前还是向后
     * @return 新下标
     */
    public static int getNextIndexInLoopList(int current, int listSize, boolean isForward) {
        int next;
        if (isForward) {
            next = current - 1;
            if (next < 0) {
                next = listSize - 1;
            }
        } else {
            next = current + 1;
            if (next >= listSize) {
                next = next % listSize;
            }
        }
        return next;
    }

    /**
     * 读取指定位置的图像
     * @param path 图像路径
     * @return BufferedImage对象
     */
    public static BufferedImage loadImage(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            LOGGER.error("Load image failed, path is {}", path, e);
        }
        return image;
    }
}
