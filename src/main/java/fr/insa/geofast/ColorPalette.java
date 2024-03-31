package fr.insa.geofast;

import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

public class ColorPalette {

    private ColorPalette() {
    }

    private static final List<Color> colors = Arrays.asList(
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.PURPLE,
            Color.ORANGE,
            Color.PINK,
            Color.CYAN,
            Color.MAGENTA,
            Color.LIME,
            Color.TEAL,
            Color.OLIVE,
            Color.MAROON,
            Color.NAVY,
            Color.AQUA,
            Color.TEAL,
            Color.VIOLET,
            Color.BROWN,
            Color.LIGHTBLUE,
            Color.LIGHTCORAL,
            Color.LIGHTCYAN,
            Color.LIGHTGOLDENRODYELLOW,
            Color.LIGHTGRAY,
            Color.LIGHTGREEN,
            Color.LIGHTPINK,
            Color.LIGHTSALMON,
            Color.LIGHTSEAGREEN,
            Color.LIGHTSKYBLUE,
            Color.LIGHTSLATEGRAY,
            Color.LIGHTSTEELBLUE,
            Color.LIGHTYELLOW,
            Color.LIMEGREEN,
            Color.LINEN,
            Color.MAGENTA,
            Color.MAROON,
            Color.MEDIUMAQUAMARINE,
            Color.MEDIUMBLUE,
            Color.MEDIUMORCHID,
            Color.MEDIUMPURPLE,
            Color.MEDIUMSEAGREEN,
            Color.MEDIUMSLATEBLUE,
            Color.MEDIUMSPRINGGREEN,
            Color.MEDIUMTURQUOISE,
            Color.MEDIUMVIOLETRED,
            Color.MIDNIGHTBLUE,
            Color.MINTCREAM,
            Color.MISTYROSE,
            Color.MOCCASIN,
            Color.NAVAJOWHITE,
            Color.NAVY,
            Color.OLDLACE,
            Color.OLIVE,
            Color.OLIVEDRAB,
            Color.ORANGE,
            Color.ORANGERED,
            Color.ORCHID,
            Color.PALEGOLDENROD,
            Color.PALEGREEN,
            Color.PALETURQUOISE,
            Color.PALEVIOLETRED,
            Color.PAPAYAWHIP,
            Color.PEACHPUFF,
            Color.PERU);

    public static Color getColor(int index) {
        return colors.get(index % colors.size());
    }
}
