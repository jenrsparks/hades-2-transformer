package io.github.jenrsparks.hades.actors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaTable;
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
        Object output = extractor.convertToNativeType(input, new java.util.HashSet<>());
        assertNull(output);
    }

    @Test
    void testConvert_nil() {
        LuaValue input = LuaValue.NIL;
        Object output = extractor.convertToNativeType(input, new java.util.HashSet<>());
        assertNull(output);
    }

    @Test
    void testConvert_boolean() {
        LuaValue input = LuaValue.valueOf(false);
        Object output = extractor.convertToNativeType(input, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(Boolean.class, output.getClass());
        assertEquals("false", output.toString());
    }

    @Test
    void testConvert_Integer() {
        LuaValue input = LuaValue.valueOf(1);
        Object output = extractor.convertToNativeType(input, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(Integer.class, output.getClass());
        assertEquals("1", output.toString());
    }

    @Test
    void testConvert_Long() {
        LuaValue input = LuaValue.valueOf(Long.MIN_VALUE);
        Object output = extractor.convertToNativeType(input, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(Long.class, output.getClass());
        assertEquals(String.valueOf(Long.MIN_VALUE), output.toString());
    }

    @Test
    void testConvert_Float() {
        LuaValue input = LuaValue.valueOf(1.234f);
        Object output = extractor.convertToNativeType(input, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(Double.class, output.getClass());
        // Floats are the worst, but rounding / truncation makes it work (enough)
        assertEquals("1.234", String.format("%.3f", (Double) output));
    }

    @Test
    void testConvert_Double() {
        LuaValue input = LuaValue.valueOf(1.234d);
        Object output = extractor.convertToNativeType(input, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(Double.class, output.getClass());
        // Floats are the worst, but rounding / truncation makes it work (enough)
        assertEquals("1.234", output.toString());
    }

    @Test
    @Disabled("Currently failing to initialize input data; not the fault of the method under test.")
    void testConvert_Table() {
        LuaValue[] keys = { LuaValue.valueOf(1) };
        LuaTable input = LuaValue.tableOf(keys);
        Object output = extractor.convertToNativeType(input, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(ArrayList.class, output.getClass());
        // Floats are the worst, but rounding / truncation makes it work (enough)
        assertEquals(1, ((ArrayList<?>) output).size());
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
