package fr.insa.geofast.utils;

import fr.insa.geofast.exceptions.IHMException;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Slf4j
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
            Color.LIME/*,
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
            Color.PERU*/);

    public static Color getColor(int index) {
        return colors.get(index % colors.size());
    }

    /**
     * Return the color name
     *
     * @param c the color tp retrieve the color from
     * @return the color name
     */
    public static String getColorName(Color c) throws IHMException {
        for (Field f : Color.class.getDeclaredFields()) {
            //we want to test only fields of type Color
            if (f.getType().equals(Color.class))
                try {
                    if (f.get(null).equals(c))
                        return f.getName().toLowerCase();
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error("Erreur lors de la récupération du nom de la couleur", e);
                }
        }
        throw new IHMException("Erreur lors de la récupération du nom de la couleur");
    }
}
