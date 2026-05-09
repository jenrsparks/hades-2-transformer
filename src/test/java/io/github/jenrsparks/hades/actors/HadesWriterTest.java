package io.github.jenrsparks.hades.actors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import io.github.jenrsparks.FileFormat;
import io.github.jenrsparks.hades.actors.impl.HadesJsonWriter;
import io.github.jenrsparks.hades.actors.impl.HadesYamlWriter;

public class HadesWriterTest {

    private Map<String, Object> testData;

    @BeforeEach
    void setUp() {
        testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", 42);
        testData.put("key3", true);
    }

    @Test
    void testGetInstance_JSON() {
        HadesWriter writer = HadesWriter.getInstance(FileFormat.JSON, testData);
        assertNotNull(writer);
        assertTrue(writer instanceof HadesJsonWriter);
    }

    @Test
    void testGetInstance_YAML() {
        HadesWriter writer = HadesWriter.getInstance(FileFormat.YAML, testData);
        assertNotNull(writer);
        assertTrue(writer instanceof HadesYamlWriter);
    }

    @Test
    void testGetInstance_withEmptyData() {
        Map<String, Object> emptyData = new HashMap<>();
        HadesWriter writer = HadesWriter.getInstance(FileFormat.JSON, emptyData);
        assertNotNull(writer);
    }

    @Test
    void testGetInstance_withNestedData() {
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nested_key", "nested_value");
        testData.put("nested", nestedMap);
        
        HadesWriter writer = HadesWriter.getInstance(FileFormat.JSON, testData);
        assertNotNull(writer);
    }

    @Test
    void testGetInstance_withListData() {
        testData.put("items", java.util.Arrays.asList("item1", "item2", "item3"));
        HadesWriter writer = HadesWriter.getInstance(FileFormat.JSON, testData);
        assertNotNull(writer);
    }

    @Test
    void testGetInstance_nullData() {
        // Test behavior with null data map
        HadesWriter writer = HadesWriter.getInstance(FileFormat.JSON, null);
        assertNotNull(writer);
    }

    @Test
    void testGetInstance_largeDataSet() {
        for (int i = 0; i < 1000; i++) {
            testData.put("key_" + i, "value_" + i);
        }
        
        HadesWriter writer = HadesWriter.getInstance(FileFormat.JSON, testData);
        assertNotNull(writer);
    }

    @Test
    void testGetInstance_specialCharactersInData() {
        testData.put("special", "value with special chars: !@#$%^&*()");
        testData.put("unicode", "unicode: 你好世界 🌍");
        
        HadesWriter writer = HadesWriter.getInstance(FileFormat.JSON, testData);
        assertNotNull(writer);
    }

    @Test
    void testFactoryPattern_consistency() {
        HadesWriter writer1 = HadesWriter.getInstance(FileFormat.JSON, testData);
        HadesWriter writer2 = HadesWriter.getInstance(FileFormat.JSON, testData);
        
        assertNotNull(writer1);
        assertNotNull(writer2);
        assertEquals(writer1.getClass(), writer2.getClass());
    }

    @Test
    void testFactoryPattern_unsupportedFormat() {
        FileFormat unsupportedFormat = FileFormat.UNSUPPORTED;

        assertThrows(IllegalArgumentException.class, () -> {
            HadesWriter.getInstance(unsupportedFormat, testData);
        });
    }
}
