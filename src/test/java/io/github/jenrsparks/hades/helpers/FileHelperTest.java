package io.github.jenrsparks.hades.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import org.junit.jupiter.api.Test;
import io.github.jenrsparks.hades.constants.WriterConstant;

/**
 * Tests for {@link FileHelper} isolated methods
 */
public class FileHelperTest {

    @Test
    void testGetOrDefaultSpecFile_default() {
        File specFile = FileHelper.getFileWithFallbackResource(null,
                WriterConstant.PASSTHROUGH_SPEC_FILE.getValue());
        assertNotNull(specFile);
        assertTrue(specFile.exists());
    }

    @Test
    void testGetOrDefaultSpecFile_invalidDefault() {
        String defaultSpecPath = WriterConstant.PASSTHROUGH_SPEC_FILE.getValue() + "-nonexistent";
        assertThrows(IllegalStateException.class, () -> {
            FileHelper.getFileWithFallbackResource(null, defaultSpecPath);
        });
    }

    @Test
    void testGetOrDefaultSpecFile_sample() {
        File inputFile = new File("src/test/resources/sample-spec.yaml");
        File specFile = FileHelper.getFileWithFallbackResource(inputFile,
                WriterConstant.PASSTHROUGH_SPEC_FILE.getValue());
        assertNotNull(specFile);
        assertTrue(specFile.exists());
        assertEquals(inputFile.getPath(), specFile.getPath());
    }

    @Test
    void testGetOrDefaultSpecFile_invalidSample() {
        File inputFile = new File("src/test/resources/sample-spec.yaml.bad");
        File specFile = FileHelper.getFileWithFallbackResource(inputFile,
                WriterConstant.PASSTHROUGH_SPEC_FILE.getValue());
        assertNotNull(specFile);
        assertTrue(specFile.exists());
        assertNotEquals(inputFile.getPath(), specFile.getPath());
    }

    @Test
    void testGetOrDefaultSpecFile_folder() {
        File inputFile = new File("src/test/resources");
        File specFile = FileHelper.getFileWithFallbackResource(inputFile,
                WriterConstant.PASSTHROUGH_SPEC_FILE.getValue());
        assertNotNull(specFile);
        assertTrue(specFile.exists());
        assertNotEquals(inputFile.getPath(), specFile.getPath());
    }


}
