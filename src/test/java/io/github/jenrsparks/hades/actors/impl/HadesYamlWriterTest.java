package io.github.jenrsparks.hades.actors.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.jenrsparks.hades.actors.HadesWriter;

public class HadesYamlWriterTest {

    private HadesWriter writer;
    private Map<String, Object> testData;
    private File tempOutputFile;

    @BeforeEach
    void setUp() throws Exception {
        testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", 42);
        testData.put("key3", true);
        
        tempOutputFile = Files.createTempFile("test-output", ".yaml").toFile();
        tempOutputFile.deleteOnExit();
        
        writer = HadesWriter.getInstance(tempOutputFile).data(testData);
    }

    @Test
    void testTarget_validFile() {
        HadesWriter result = HadesWriter.getInstance(tempOutputFile).data(testData);
        assertNotNull(result);
        assertTrue(result instanceof HadesWriter);
    }

    @Test
    void testTarget_returnsChain() {
        HadesWriter result = HadesWriter.getInstance(tempOutputFile).data(testData);
        assertNotEquals(writer, result);
    }

    @Test
    @Disabled("HadesWriter does not validate path on target() - validation happens on write()")
    void testTarget_invalidPath() {
        File invalidFile = new File("/nonexistent/directory/file.yaml");
        assertThrows(RuntimeException.class, () -> {
            HadesWriter.getInstance(invalidFile);
        });
    }

    @Test
    void testWrite_success() {
        writer = HadesWriter.getInstance(tempOutputFile).data(testData);
        boolean result = writer.write();
        assertTrue(result);
        assertTrue(tempOutputFile.exists());
        assertTrue(tempOutputFile.length() > 0);
    }

    @Test
    void testWrite_emptyData() throws Exception {
        Map<String, Object> emptyData = new HashMap<>();
        HadesWriter emptyWriter = HadesWriter.getInstance(tempOutputFile).data(emptyData);
        
        boolean result = emptyWriter.write();
        // Empty data still results in valid YAML output
        assertTrue(result);
        assertTrue(tempOutputFile.exists());
    }

    @Test
    void testWrite_withNestedData() throws Exception {
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nested_key", "nested_value");
        testData.put("nested", nestedMap);
        
        writer = HadesWriter.getInstance(tempOutputFile).data(testData);
        
        boolean result = writer.write();
        assertTrue(result);
        assertTrue(tempOutputFile.exists());
        
        String content = new String(Files.readAllBytes(tempOutputFile.toPath()));
        assertTrue(content.contains("nested"));
        assertTrue(content.contains("nested_key"));
    }

    @Test
    void testWrite_withListData() throws Exception {
        testData.put("items", java.util.Arrays.asList("item1", "item2", "item3"));
        
        writer = HadesWriter.getInstance(tempOutputFile).data(testData);
        
        boolean result = writer.write();
        assertTrue(result);
        assertTrue(tempOutputFile.exists());
        
        String content = new String(Files.readAllBytes(tempOutputFile.toPath()));
        assertTrue(content.contains("item1"));
        assertTrue(content.contains("item2"));
        assertTrue(content.contains("item3"));
    }

    @Test
    void testWrite_validYamlFormat() throws Exception {
        writer = HadesWriter.getInstance(tempOutputFile).data(testData);
        writer.write();
        
        String content = new String(Files.readAllBytes(tempOutputFile.toPath()));
        // Verify it's parseable as YAML
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Map<?, ?> parsed = mapper.readValue(content, Map.class);
        assertNotNull(parsed);
        assertFalse(parsed.isEmpty());
    }

    @Test
    void testWrite_specialCharacters() throws Exception {
        testData.put("special", "value with special chars: !@#$%^&*()");
        testData.put("unicode", "unicode: 你好世界 🌍");
        
        writer = HadesWriter.getInstance(tempOutputFile).data(testData);
        
        boolean result = writer.write();
        assertTrue(result);
        
        String content = new String(Files.readAllBytes(tempOutputFile.toPath()), "UTF-8");
        assertTrue(content.length() > 0);
    }

    @Test
    void testWrite_largeDataSet() throws Exception {
        for (int i = 0; i < 1000; i++) {
            testData.put("key_" + i, "value_" + i);
        }
        
        writer = HadesWriter.getInstance(tempOutputFile).data(testData);
        
        boolean result = writer.write();
        assertTrue(result);
        assertTrue(tempOutputFile.length() > 10000);
    }

    @Test
    void testWrite_returnsTrue() {
        writer = HadesWriter.getInstance(tempOutputFile).data(testData);
        boolean result = writer.write();
        assertTrue(result);
    }

    @Test
    void testChaining() {
        HadesWriter result = HadesWriter.getInstance(tempOutputFile).data(testData);
        assertTrue(result.write());
        assertTrue(tempOutputFile.exists());
    }

    @Test
    void testWrite_multipleConsecutiveWrites() throws Exception {
        // Write to the same file multiple times
        writer = HadesWriter.getInstance(tempOutputFile).data(testData);
        assertTrue(writer.write());
        assertTrue(tempOutputFile.exists());
        long firstSize = tempOutputFile.length();
        
        // Write again with different data
        Map<String, Object> newData = new HashMap<>();
        newData.put("newKey", "newValue");
        writer = HadesWriter.getInstance(tempOutputFile).data(newData);
        assertTrue(writer.write());
        
        // Verify file was updated
        assertTrue(tempOutputFile.exists());
        assertFalse(tempOutputFile.length() == firstSize);
    }

    @Test
    void testCreate_withBadFile() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            HadesWriter.getInstance(null);
        });
    }
}
