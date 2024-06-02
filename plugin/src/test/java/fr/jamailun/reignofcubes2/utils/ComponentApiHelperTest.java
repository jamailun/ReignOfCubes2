package fr.jamailun.reignofcubes2.utils;

import fr.jamailun.reignofcubes2.api.utils.ComponentApiHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComponentApiHelperTest {

    @Test
    void conversionEmpty() {
        testConversion("", "");
    }

    @Test
    void conversionNone() {
        testConversion("test", "test");
    }

    @Test
    void conversionColor() {
        testConversion("&3 TEST", "<dark_aqua> TEST");
    }

    @Test
    void conversionDoubleStart() {
        testConversion("A &&a B", "A &<green> B");
    }

    @Test
    void conversionComplex() {
        testConversion("&6Salut les &lamis &4!", "<gold>Salut les <b>amis <dark_red>!");
    }

    private void testConversion(String toConvert, String expected) {
        String converted = ComponentApiHelper.convertLegacy(toConvert);
        Assertions.assertEquals(expected, converted);
    }

}
