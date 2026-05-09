package io.github.jenrsparks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import org.junit.jupiter.api.Test;

public class FileFormatTest {

    @Test
    void testGetFileFormat_JSON() {
        File jsonFile = new File("test.json");
        FileFormat format = FileFormat.getFileFormat(jsonFile);
        assertEquals(FileFormat.JSON, format);
    }

    @Test
    void testGetFileFormat_JSON_uppercase() {
        File jsonFile = new File("TEST.JSON");
        FileFormat format = FileFormat.getFileFormat(jsonFile);
        assertEquals(FileFormat.JSON, format);
    }

    @Test
    void testGetFileFormat_YAML_yml() {
        File yamlFile = new File("test.yml");
        FileFormat format = FileFormat.getFileFormat(yamlFile);
        assertEquals(FileFormat.YAML, format);
    }

    @Test
    void testGetFileFormat_YAML_yaml() {
        File yamlFile = new File("test.yaml");
        FileFormat format = FileFormat.getFileFormat(yamlFile);
        assertEquals(FileFormat.YAML, format);
    }

    @Test
    void testGetFileFormat_YAML_uppercase() {
        File yamlFile = new File("TEST.YAML");
        FileFormat format = FileFormat.getFileFormat(yamlFile);
        assertEquals(FileFormat.YAML, format);
    }

    @Test
    void testGetFileFormat_UnsupportedFormat() {
        File unsupportedFile = new File("test.latex");
        assertThrows(IllegalArgumentException.class, () -> {
            FileFormat.getFileFormat(unsupportedFile);
        });
    }

    @Test
    void testGetFileFormat_NoExtension() {
        File noExtFile = new File("testfile");
        assertThrows(IllegalArgumentException.class, () -> {
            FileFormat.getFileFormat(noExtFile);
        });
    }

    @Test
    void testGetFileFormat_MultiDotPath() {
        File jsonFile = new File("/path/to/my.file.json");
        FileFormat format = FileFormat.getFileFormat(jsonFile);
        assertEquals(FileFormat.JSON, format);
    }

    @Test
    void testGetFileFormat_XMLFormat() {
        File xmlFile = new File("test.fail");
        assertThrows(IllegalArgumentException.class, () -> {
            FileFormat.getFileFormat(xmlFile);
        });
    }
}
