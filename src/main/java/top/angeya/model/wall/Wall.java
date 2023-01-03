package top.angeya.model.wall;

import top.angeya.model.VisibleObject;

/**
 * 墙
 * @author: Angeya
 * @date: 2022/12/31 17:44
 **/
public abstract class Wall extends VisibleObject {


    public Wall(int x, int y, String imagePath) {
        super(x, y, imagePath);
    }
}
