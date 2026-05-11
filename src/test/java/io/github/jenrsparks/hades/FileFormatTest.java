package io.github.jenrsparks.hades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

public class FileFormatTest {

    @Test
    void testGetFileFormat_JSON() throws IOException {
        File tempFile = Files.createTempFile("test-output", ".json").toFile();
        tempFile.deleteOnExit();

        FileFormat format = FileFormat.getFileFormat(tempFile);
        assertEquals(FileFormat.JSON, format);
    }

    @Test
    void testGetFileFormat_JSON_uppercase() throws IOException {
        File tempFile = Files.createTempFile("TEST", ".JSON").toFile();
        tempFile.deleteOnExit();

        FileFormat format = FileFormat.getFileFormat(tempFile);
        assertEquals(FileFormat.JSON, format);
    }

    @Test
    void testGetFileFormat_YAML_yml() throws IOException {
        File tempFile = Files.createTempFile("test-output", ".yml").toFile();
        tempFile.deleteOnExit();

        FileFormat format = FileFormat.getFileFormat(tempFile);
        assertEquals(FileFormat.YAML, format);
    }

    @Test
    void testGetFileFormat_YAML_yaml() throws IOException {
        File tempFile = Files.createTempFile("test-output", ".yaml").toFile();
        tempFile.deleteOnExit();

        FileFormat format = FileFormat.getFileFormat(tempFile);
        assertEquals(FileFormat.YAML, format);
    }

    @Test
    void testGetFileFormat_YAML_uppercase() throws IOException {
        File tempFile = Files.createTempFile("TEST", ".YAML").toFile();
        tempFile.deleteOnExit();

        FileFormat format = FileFormat.getFileFormat(tempFile);
        assertEquals(FileFormat.YAML, format);
    }

    @Test
    void testGetFileFormat_UnsupportedFormat() throws IOException {
        File tempFile = Files.createTempFile("test", ".latex").toFile();
        tempFile.deleteOnExit();
        assertThrows(IllegalArgumentException.class, () -> {
            FileFormat.getFileFormat(tempFile);
        });
    }

    @Test
    void testGetFileFormat_NoExtension() throws IOException {
        File tempFile = Files.createTempFile("test-output", "").toFile();
        tempFile.deleteOnExit();
        assertThrows(IllegalArgumentException.class, () -> {
            FileFormat.getFileFormat(tempFile);
        });
    }

    @Test
    void testGetFileFormat_XMLFormat() throws IOException {
        File tempFile = Files.createTempFile("test", ".fail").toFile();
        tempFile.deleteOnExit();
        assertThrows(IllegalArgumentException.class, () -> {
            FileFormat.getFileFormat(tempFile);
        });
    }
}
