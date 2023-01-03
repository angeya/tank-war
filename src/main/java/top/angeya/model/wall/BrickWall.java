package top.angeya.model.wall;

import top.angeya.constant.ImagePaths;

/**
 * @author: Angeya
 * @date: 2022/12/31 17:47
 **/
public class BrickWall extends Wall{

    public BrickWall(int x, int y) {
        super(x, y, ImagePaths.BRICK_WALL);
    }
}
