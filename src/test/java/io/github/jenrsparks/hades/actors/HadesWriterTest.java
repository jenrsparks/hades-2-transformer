package io.github.jenrsparks.hades.actors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
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
        HadesWriter writer = HadesWriter.getInstance(new File("test.json"));
        assertNotNull(writer);
        assertTrue(writer instanceof HadesJsonWriter);
    }

    @Test
    void testGetInstance_YAML() {
        HadesWriter writer = HadesWriter.getInstance(new File("test.yaml"));
        assertNotNull(writer);
        assertTrue(writer instanceof HadesYamlWriter);
    }

    @Test
    void testGetInstance_withEmptyData() {
        Map<String, Object> emptyData = new HashMap<>();
        HadesWriter writer = HadesWriter.getInstance(new File("test.json")).data(emptyData);
        assertNotNull(writer);
    }

    @Test
    void testGetInstance_withNestedData() {
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nested_key", "nested_value");
        testData.put("nested", nestedMap);
        
        HadesWriter writer = HadesWriter.getInstance(new File("test.json")).data(testData);
        assertNotNull(writer);
    }

    @Test
    void testGetInstance_withListData() {
        testData.put("items", java.util.Arrays.asList("item1", "item2", "item3"));
        HadesWriter writer = HadesWriter.getInstance(new File("test.json")).data(testData);
        assertNotNull(writer);
    }

    @Test
    void testGetInstance_nullData() {
        // Test behavior with null data map
        HadesWriter writer = HadesWriter.getInstance(new File("test.json")).data(null);
        assertNotNull(writer);
    }

    @Test
    void testGetInstance_largeDataSet() {
        for (int i = 0; i < 1000; i++) {
            testData.put("key_" + i, "value_" + i);
        }
        
        HadesWriter writer = HadesWriter.getInstance(new File("test.json")).data(testData);
        assertNotNull(writer);
    }

    @Test
    void testGetInstance_specialCharactersInData() {
        testData.put("special", "value with special chars: !@#$%^&*()");
        testData.put("unicode", "unicode: 你好世界 🌍");
        
        HadesWriter writer = HadesWriter.getInstance(new File("test.json")).data(testData);
        assertNotNull(writer);
    }

    @Test
    void testFactoryPattern_consistency() {
        HadesWriter writer1 = HadesWriter.getInstance(new File("test.json")).data(testData);
        HadesWriter writer2 = HadesWriter.getInstance(new File("test.json")).data(testData);
        
        assertNotNull(writer1);
        assertNotNull(writer2);
        assertEquals(writer1.getClass(), writer2.getClass());
    }

    @Test
    void testFactoryPattern_unsupportedFormat() {

        assertThrows(IllegalArgumentException.class, () -> {
            HadesWriter.getInstance(new File("test.txt"));
        });
    }
}
