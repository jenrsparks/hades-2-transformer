package io.github.jenrsparks.hades.actors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        LuaTable table = LuaValue.tableOf();
        table.set(1, LuaValue.valueOf(10));
        table.set(2, LuaValue.valueOf(20));
        table.set(3, LuaValue.valueOf(30));
        List<Object> output = extractor.convertList(table, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(3, output.size());
        assertEquals(10, output.get(0));
        assertEquals(20, output.get(1));
        assertEquals(30, output.get(2));
    }

    @Test
    void testConvertList_withNestedValues() {
        LuaTable innerTable = LuaValue.tableOf();
        innerTable.set("key", LuaValue.valueOf("value"));
        LuaTable table = LuaValue.tableOf();
        table.set(1, LuaValue.valueOf("item1"));
        table.set(2, innerTable);
        table.set(3, LuaValue.valueOf("item3"));
        List<Object> output = extractor.convertList(table, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(3, output.size());
        assertEquals("item1", output.get(0));
        assertNotNull(output.get(1));
        assertTrue(output.get(1) instanceof Map);
    }

    @Test
    void testConvertList_empty() {
        LuaTable table = LuaValue.tableOf();
        List<Object> output = extractor.convertList(table, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(0, output.size());
    }

    @Test
    void testConvertMap() {
        LuaTable table = LuaValue.tableOf();
        table.set("key1", LuaValue.valueOf("value1"));
        table.set("key2", LuaValue.valueOf(42));
        table.set("key3", LuaValue.valueOf(true));
        
        Map<String, Object> output = extractor.convertMap(table, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(3, output.size());
        assertEquals("value1", output.get("key1"));
        assertEquals(42, output.get("key2"));
        assertEquals(true, output.get("key3"));
    }

    @Test
    void testConvertMap_withNestedMap() {
        LuaTable innerTable = LuaValue.tableOf();
        innerTable.set("nested_key", LuaValue.valueOf("nested_value"));
        
        LuaTable table = LuaValue.tableOf();
        table.set("key1", LuaValue.valueOf("value1"));
        table.set("nested", innerTable);
        
        Map<String, Object> output = extractor.convertMap(table, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(2, output.size());
        assertEquals("value1", output.get("key1"));
        Object nestedObj = output.get("nested");
        assertNotNull(nestedObj);
        assertTrue(nestedObj instanceof Map);
        if(nestedObj instanceof Map) {
            Map<?, ?> rawMap = (Map<?, ?>) nestedObj;
            assertEquals(1, rawMap.size());
            assertEquals(String.class, rawMap.get("nested_key").getClass());
            assertEquals("nested_value", rawMap.get("nested_key"));
        }
    }

    @Test
    void testConvertMap_empty() {
        LuaTable table = LuaValue.tableOf();
        Map<String, Object> output = extractor.convertMap(table, new java.util.HashSet<>());
        assertNotNull(output);
        assertEquals(0, output.size());
    }

    @Test
    void testConvertMap_withNullValues() {
        LuaTable table = LuaValue.tableOf();
        table.set("key1", LuaValue.valueOf("value1"));
        table.set("key2", LuaValue.NIL);
        table.set("key3", LuaValue.valueOf(100));
        
        Map<String, Object> output = extractor.convertMap(table, new java.util.HashSet<>());
        assertNotNull(output);
        // NIL values should not be included in the map
        assertEquals(2, output.size());
        assertEquals("value1", output.get("key1"));
        assertEquals(100, output.get("key3"));
    }

    @Test
    // @Disabled("Requires a valid Lua file for testing; integration test candidate")
    void testExtract() {
        // Use resources/dummy.lua for testing
        File testLuaFile = new File("src/test/resources/dummy.lua");
        Map<String, Object> output = extractor.extract(testLuaFile);
        assertNotNull(output);
        assertEquals(7, output.size());
    }
}
