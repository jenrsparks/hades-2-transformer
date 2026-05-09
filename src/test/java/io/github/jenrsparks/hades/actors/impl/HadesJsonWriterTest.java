package io.github.jenrsparks.hades.actors.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.jenrsparks.hades.actors.HadesWriter;

public class HadesJsonWriterTest {

    private HadesJsonWriter writer;
    private Map<String, Object> testData;
    private File tempOutputFile;

    @BeforeEach
    void setUp() throws Exception {
        testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", 42);
        testData.put("key3", true);
        
        tempOutputFile = Files.createTempFile("test-output", ".json").toFile();
        tempOutputFile.deleteOnExit();
        
        writer = new HadesJsonWriter(testData);
    }

    @Test
    void testTarget_validFile() {
        HadesWriter result = writer.target(tempOutputFile);
        assertNotNull(result);
        assertTrue(result instanceof HadesJsonWriter);
    }

    @Test
    void testTarget_returnsChain() {
        HadesWriter result = writer.target(tempOutputFile);
        assertEquals(writer, result);
    }

    @Test
    void testTarget_invalidPath() {
        File invalidFile = new File("/nonexistent/directory/file.json");
        assertThrows(RuntimeException.class, () -> {
            writer.target(invalidFile);
        });
    }

    @Test
    void testWrite_success() {
        writer.target(tempOutputFile);
        boolean result = writer.write();
        assertTrue(result);
        assertTrue(tempOutputFile.exists());
        assertTrue(tempOutputFile.length() > 0);
    }

    @Test
    void testWrite_emptyData() throws Exception {
        Map<String, Object> emptyData = new HashMap<>();
        HadesJsonWriter emptyWriter = new HadesJsonWriter(emptyData);
        emptyWriter.target(tempOutputFile);
        
        boolean result = emptyWriter.write();
        assertTrue(result);
        assertTrue(tempOutputFile.exists());
        
        // Verify the file contains valid JSON
        String content = new String(Files.readAllBytes(tempOutputFile.toPath()));
        assertEquals("{}", content);
    }

    @Test
    void testWrite_withNestedData() throws Exception {
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nested_key", "nested_value");
        testData.put("nested", nestedMap);
        
        writer = new HadesJsonWriter(testData);
        writer.target(tempOutputFile);
        
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
        
        writer = new HadesJsonWriter(testData);
        writer.target(tempOutputFile);
        
        boolean result = writer.write();
        assertTrue(result);
        assertTrue(tempOutputFile.exists());
        
        String content = new String(Files.readAllBytes(tempOutputFile.toPath()));
        assertTrue(content.contains("item1"));
    }

    @Test
    void testWrite_prettyPrinting() throws Exception {
        writer.target(tempOutputFile);
        writer.write();
        
        String content = new String(Files.readAllBytes(tempOutputFile.toPath()));
        // Pretty printing adds newlines and indentation
        assertTrue(content.contains("\n"));
    }

    @Test
    void testWrite_validJsonFormat() throws Exception {
        writer.target(tempOutputFile);
        writer.write();
        
        String content = new String(Files.readAllBytes(tempOutputFile.toPath()));
        // Verify it's parseable as JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<?, ?> parsed = gson.fromJson(content, Map.class);
        assertNotNull(parsed);
        assertEquals(3, parsed.size());
    }

    @Test
    void testWrite_specialCharacters() throws Exception {
        testData.put("special", "value with special chars: !@#$%^&*()");
        testData.put("unicode", "unicode: 你好世界 🌍");
        
        writer = new HadesJsonWriter(testData);
        writer.target(tempOutputFile);
        
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
        
        writer = new HadesJsonWriter(testData);
        writer.target(tempOutputFile);
        
        boolean result = writer.write();
        assertTrue(result);
        assertTrue(tempOutputFile.length() > 10000);
    }

    @Test
    void testChaining() {
        HadesWriter result = writer.target(tempOutputFile);
        assertTrue(result.write());
        assertTrue(tempOutputFile.exists());
    }
}
