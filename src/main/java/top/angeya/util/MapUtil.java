package top.angeya.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.angeya.constant.Const;
import top.angeya.enums.WallType;
import top.angeya.model.wall.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * 地图相关处理类
 * @author: Angeya
 * @date: 2022/12/31 17:56
 **/
public class MapUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapUtil.class);

    /**
     * 地图文件地址
     */
    public final static String MAP_FILE_PATH = "map/";
    /**
     * 地图数据文件后缀
     */
    public final static String MAP_FILE_SUFFIX = ".map";

    private MapUtil() {
    }

    public static List<Wall> loadMapByLevel(int level) {
        List<Wall> wallList = new LinkedList<>();
        List<String> dataList = loadMapFromFileByLevel(level);
        dataList.forEach(data -> wallList.addAll(parseWallByMapData(data)));
        return wallList;
    }

    private static List<String> loadMapFromFileByLevel(int level) {
        Path path = Paths.get(MAP_FILE_PATH + level + MAP_FILE_SUFFIX);
        List<String> dataList;
        try {
            dataList = Files.readAllLines(path);
        } catch (IOException e) {
            LOGGER.error("Load map failed, path is {}", path, e);
            throw new RuntimeException(e);
        }
        return dataList;
    }

    private static List<Wall> parseWallByMapData(String mapData) {
        List<Wall> wallList = new LinkedList<>();
        try {
            // 获取墙类型和墙数据
            String[] typeAndData = mapData.split(Const.WALL_TYPE_SPLITTER);
            String wallType = typeAndData[0];
            String wallData = typeAndData[1];
            // 获取每一片墙的坐标数据
            String[] dataItems = wallData.split(Const.WALL_DATA_SPLITTER);
            for (String dataItem : dataItems) {
                Wall wall;
                // 获取每一片墙的x和y坐标
                String[] xAndY = dataItem.split(Const.WALL_COORDINATE_SPLITTER);
                int x = Integer.parseInt(xAndY[0]);
                int y = Integer.parseInt(xAndY[1]);
                // 根据类型创建墙
                if (WallType.BRICK.name().equals(wallType)) {
                    wall = new BrickWall(x, y);
                } else if (WallType.GRASS.name().equals(wallType)) {
                    wall = new GrassWall(x, y);
                } else if (WallType.RIVER.name().equals(wallType)) {
                    wall = new RiverWall(x, y);
                } else if (WallType.IRON.name().equals(wallType)) {
                    wall = new IronWall(x, y);
                } else {
                    wall = new BrickWall(x, y);
                }
                wallList.add(wall);
            }
        } catch (Exception e) {
            LOGGER.error("Parse wall data failed, map data is: {}", mapData, e);
            throw new RuntimeException(e);
        }
        return wallList;
    }

    /**
     * 获取基地的防护砖块
     * @return 砖块集合
     */
    public static List<Wall> getHomeWallList() {
        List<Wall> wallList = new LinkedList<>();
        wallList.add(new BrickWall(347, 512));
        wallList.add(new BrickWall(367, 512));
        wallList.add(new BrickWall(387, 512));

        wallList.add(new BrickWall(347, 532));
        wallList.add(new BrickWall(347, 552));
        wallList.add(new BrickWall(347, 572));

        wallList.add(new BrickWall(407, 512));
        wallList.add(new BrickWall(407, 532));
        wallList.add(new BrickWall(407, 552));
        wallList.add(new BrickWall(407, 572));
        return wallList;
    }

}
