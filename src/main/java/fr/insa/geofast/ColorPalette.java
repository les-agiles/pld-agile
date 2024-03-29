package fr.insa.geofast;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ColorPalette {

    private ColorPalette() {
    }

    private static final List<Color> colors = new ArrayList<>(
            java.util.Arrays.asList(
                    javafx.scene.paint.Color.RED,
                    javafx.scene.paint.Color.BLUE,
                    javafx.scene.paint.Color.GREEN,
                    javafx.scene.paint.Color.YELLOW,
                    javafx.scene.paint.Color.PURPLE,
                    javafx.scene.paint.Color.ORANGE,
                    javafx.scene.paint.Color.PINK,
                    javafx.scene.paint.Color.CYAN,
                    javafx.scene.paint.Color.MAGENTA,
                    javafx.scene.paint.Color.LIME,
                    javafx.scene.paint.Color.TEAL,
                    javafx.scene.paint.Color.OLIVE,
                    javafx.scene.paint.Color.MAROON,
                    javafx.scene.paint.Color.NAVY,
                    javafx.scene.paint.Color.AQUA,
                    javafx.scene.paint.Color.TEAL,
                    javafx.scene.paint.Color.VIOLET,
                    javafx.scene.paint.Color.BROWN,
                    javafx.scene.paint.Color.LIGHTBLUE,
                    javafx.scene.paint.Color.LIGHTCORAL,
                    javafx.scene.paint.Color.LIGHTCYAN,
                    javafx.scene.paint.Color.LIGHTGOLDENRODYELLOW,
                    javafx.scene.paint.Color.LIGHTGRAY,
                    javafx.scene.paint.Color.LIGHTGREEN,
                    javafx.scene.paint.Color.LIGHTPINK,
                    javafx.scene.paint.Color.LIGHTSALMON,
                    javafx.scene.paint.Color.LIGHTSEAGREEN,
                    javafx.scene.paint.Color.LIGHTSKYBLUE,
                    javafx.scene.paint.Color.LIGHTSLATEGRAY,
                    javafx.scene.paint.Color.LIGHTSTEELBLUE,
                    javafx.scene.paint.Color.LIGHTYELLOW,
                    javafx.scene.paint.Color.LIMEGREEN,
                    javafx.scene.paint.Color.LINEN,
                    javafx.scene.paint.Color.MAGENTA,
                    javafx.scene.paint.Color.MAROON,
                    javafx.scene.paint.Color.MEDIUMAQUAMARINE,
                    javafx.scene.paint.Color.MEDIUMBLUE,
                    javafx.scene.paint.Color.MEDIUMORCHID,
                    javafx.scene.paint.Color.MEDIUMPURPLE,
                    javafx.scene.paint.Color.MEDIUMSEAGREEN,
                    javafx.scene.paint.Color.MEDIUMSLATEBLUE,
                    javafx.scene.paint.Color.MEDIUMSPRINGGREEN,
                    javafx.scene.paint.Color.MEDIUMTURQUOISE,
                    javafx.scene.paint.Color.MEDIUMVIOLETRED,
                    javafx.scene.paint.Color.MIDNIGHTBLUE,
                    javafx.scene.paint.Color.MINTCREAM,
                    javafx.scene.paint.Color.MISTYROSE,
                    javafx.scene.paint.Color.MOCCASIN,
                    javafx.scene.paint.Color.NAVAJOWHITE,
                    javafx.scene.paint.Color.NAVY,
                    javafx.scene.paint.Color.OLDLACE,
                    javafx.scene.paint.Color.OLIVE,
                    javafx.scene.paint.Color.OLIVEDRAB,
                    javafx.scene.paint.Color.ORANGE,
                    javafx.scene.paint.Color.ORANGERED,
                    javafx.scene.paint.Color.ORCHID,
                    javafx.scene.paint.Color.PALEGOLDENROD,
                    javafx.scene.paint.Color.PALEGREEN,
                    javafx.scene.paint.Color.PALETURQUOISE,
                    javafx.scene.paint.Color.PALEVIOLETRED,
                    javafx.scene.paint.Color.PAPAYAWHIP,
                    javafx.scene.paint.Color.PEACHPUFF,
                    javafx.scene.paint.Color.PERU)
    );

    public static javafx.scene.paint.Color getColor(int index) {
        return colors.get(index % colors.size());
    }
}
