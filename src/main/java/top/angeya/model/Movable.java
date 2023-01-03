package top.angeya.model;

/**
 *
 * 可移动接口
 * @author: Angeya
 * @date: 2023/1/1 22:37
 **/
public interface Movable {

    /**
     * 移动
     */
    void move();

    /**
     * 超越边界
     */
    void beyondBoundary();

}
