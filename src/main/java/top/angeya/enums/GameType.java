package top.angeya.enums;

import top.angeya.constant.Const;

/**
 * @author: Angeya
 * @date: 2022/12/31 14:06
 **/
public enum GameType {
    /**
     * 单人游戏
     */
    ONE_PLAYER(400, Const.ONE_PLAYER),
    /**
     * 双人游戏
     */
    TWO_PLAYER(460, Const.TWO_PLAYER),
    ;

    GameType(int position, String desc) {
        this.position = position;
        this.desc = desc;
    }

    private int position;

    private String desc;

    public int getPosition() {
        return position;
    }

    public String getDesc() {
        return desc;
    }
}
