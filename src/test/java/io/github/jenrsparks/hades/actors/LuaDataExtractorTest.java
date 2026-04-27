package io.github.jenrsparks.hades.actors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaValue;

public class LuaDataExtractorTest {

    private LuaDataExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new LuaDataExtractor();
    }

    @Test
    void testConvert_null() {
        LuaValue input = null;
        Object output = extractor.convert(input);
        assertNull(output);
    }

    @Test
    void testConvert_boolean() {
        LuaValue input = LuaValue.valueOf(false);
        Object output = extractor.convert(input);
        assertNotNull(output);
        assertEquals(output.getClass(), Boolean.class);
        assertEquals(output.toString(), "false");
    }

    @Test
    void testConvert_Integer() {
        LuaValue input = LuaValue.valueOf(1);
        Object output = extractor.convert(input);
        assertNotNull(output);
        assertEquals(output.getClass(), Integer.class);
        assertEquals(output.toString(), "1");
    }

    @Test
    void testConvertList() {

    }

    @Test
    void testConvertMap() {

    }

    @Test
    void testExtract() {

    }
}
