package fr.insa.geofast.utils;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IconsHelperTest {
    @Test
    void getIcon_ShouldGetIcon(){
        SVGPath icon = IconsHelper.getIcon("clock-icon", Color.BLACK, null);
        assertNotNull(icon);
    }

    @Test
    void getIcon_ShouldNotFindIconAndReturnNull(){
        SVGPath icon = IconsHelper.getIcon("xoxo", Color.BLACK, null);
        assertNull(icon);
    }
}
