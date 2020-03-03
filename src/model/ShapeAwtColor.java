package model;

import java.awt.*;
import java.util.EnumMap;

/**
 * Uses a "static initialization method" to create a static EnumMap
 * This maps the enum ShapeColor to java.awt.Color
 *
 * @author Moya Richards
 */
public class ShapeAwtColor {
    private static EnumMap<ShapeColor, java.awt.Color> map = prepareMap();

    private static EnumMap prepareMap() {
        EnumMap<ShapeColor, java.awt.Color> map = new EnumMap<ShapeColor, Color>(ShapeColor.class);

        map.put(ShapeColor.BLACK, Color.BLACK);
        map.put(ShapeColor.BLUE, Color.BLUE);
        map.put(ShapeColor.CYAN, Color.CYAN);
        map.put(ShapeColor.DARK_GRAY, Color.DARK_GRAY);
        map.put(ShapeColor.GRAY, Color.GRAY);
        map.put(ShapeColor.GREEN, Color.GREEN);
        map.put(ShapeColor.LIGHT_GRAY, Color.LIGHT_GRAY);
        map.put(ShapeColor.MAGENTA, Color.MAGENTA);
        map.put(ShapeColor.ORANGE, Color.ORANGE);
        map.put(ShapeColor.PINK, Color.PINK);
        map.put(ShapeColor.RED, Color.RED);
        map.put(ShapeColor.WHITE, Color.WHITE);
        map.put(ShapeColor.YELLOW, Color.YELLOW);
        return map;
    }

    /**
     * Use the shapeColor name to get an instance of java.awt.Color
     *
     * @author Moya Richards
     * @link https://www.daniweb.com/posts/jump/1452191
     */
    public static Color getColor(ShapeColor shapeColor) {
        return map.get(shapeColor);
    }
}
