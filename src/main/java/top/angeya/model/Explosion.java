package top.angeya.model;

import top.angeya.constant.ImagePaths;

/**
 * 爆炸
 * @author: Angeya
 * @date: 2023/1/2 13:43
 **/
public class Explosion extends VisibleObject{

    /**
     * 爆炸显示时间
     */
    private static final int ALIVE_TIME = 500;

    private final long creationTime = System.currentTimeMillis();

    public Explosion(int x, int y) {
        super(x, y, ImagePaths.BOOM);
    }

    @Override
    public boolean isAlive() {
        long now = System.currentTimeMillis();
        return now - creationTime < ALIVE_TIME;
    }
}
