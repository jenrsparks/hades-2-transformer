package io.github.jenrsparks.hades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import picocli.CommandLine;

/**
 * Integration tests for App using the dummy Lua test resource.
 */
public class AppTest {

    private App app;
    private File dummyLuaFile;

    @BeforeEach
    void setUp() throws Exception {
        app = new App();
        dummyLuaFile = getResourceFile("/dummy.lua");
    }

    @Test
    void testApp_instantiation() {
        assertNotNull(app);
    }

    @Test
    void testApp_implementsRunnable() {
        assertTrue(app instanceof Runnable);
    }

    @Test
    void testReportWriteResult_success() {
        App reportApp = new App();
        assertEquals(true, reportApp.reportWriteResult(true, dummyLuaFile));
    }

    @Test
    void testReportWriteResult_failure() {
        App reportApp = new App();
        assertEquals(false, reportApp.reportWriteResult(false, dummyLuaFile));
    }

    @Test
    void testApp_run_withDefaults() throws Exception {
        App defaultApp = new App();
        File outputFile = Files.createTempFile("default-output", ".json").toFile();
        outputFile.deleteOnExit();

        setPrivateField(defaultApp, "inputFile", dummyLuaFile);
        setPrivateField(defaultApp, "outputFile", outputFile);

        defaultApp.run();

        assertTrue(outputFile.exists());
        String content = Files.readString(outputFile.toPath());
        assertTrue(content.contains("\"MAGIC\""));
        assertTrue(content.trim().startsWith("{"));
    }

    @Test
    void testApp_run_withCustomInput() throws Exception {
        File outputFile = Files.createTempFile("custom-input-output", ".json").toFile();
        outputFile.deleteOnExit();

        App customApp = new App();
        new CommandLine(customApp).parseArgs("-i", dummyLuaFile.getAbsolutePath(), "-o",
                outputFile.getAbsolutePath());
        customApp.run();

        assertTrue(outputFile.exists());
        assertTrue(Files.size(outputFile.toPath()) > 0);
        String content = Files.readString(outputFile.toPath());
        assertTrue(content.contains("\"MAGIC\""));
    }

    @Test
    void testApp_run_withCustomOutput() throws Exception {
        File outputFile = Files.createTempFile("custom-output", ".json").toFile();
        outputFile.deleteOnExit();

        App customApp = new App();
        new CommandLine(customApp).parseArgs("-i", dummyLuaFile.getAbsolutePath(), "-o",
                outputFile.getAbsolutePath());
        customApp.run();

        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
    }

    @Test
    void testApp_run_withJoltSpec() throws Exception {
        String specContent = "- operation: shift\n  spec:\n    MAGIC: magic\n";
        Path specPath = Files.createTempFile("custom-spec", ".yaml");
        Files.writeString(specPath, specContent);
        specPath.toFile().deleteOnExit();

        File outputFile = Files.createTempFile("spec-output", ".json").toFile();
        outputFile.deleteOnExit();

        App customApp = new App();
        new CommandLine(customApp).parseArgs("-i", dummyLuaFile.getAbsolutePath(), "-o",
                outputFile.getAbsolutePath(), "-s", specPath.toAbsolutePath().toString());
        customApp.run();

        assertTrue(outputFile.exists());
        String content = Files.readString(outputFile.toPath());
        assertTrue(content.contains("\"magic\""));
        assertTrue(content.contains("826427219"));
    }

    @Test
    void testApp_run_invalidInput() throws Exception {
        File outputFile = Files.createTempFile("invalid-input-output", ".json").toFile();
        outputFile.deleteOnExit();

        App invalidApp = new App();
        new CommandLine(invalidApp).parseArgs("-i", "/tmp/does-not-exist.lua", "-o",
                outputFile.getAbsolutePath());

        assertThrows(Exception.class, invalidApp::run);
    }

    @Test
    void testApp_run_invalidOutputPath() throws Exception {
        File invalidOutput = new File("/does/not/exist/output.json");

        App invalidApp = new App();
        new CommandLine(invalidApp).parseArgs("-i", dummyLuaFile.getAbsolutePath(), "-o",
                invalidOutput.getAbsolutePath());

        assertThrows(Exception.class, invalidApp::run);
    }

    @Test
    void testApp_processesValidLuaFile() throws Exception {
        File outputFile = Files.createTempFile("valid-lua-output", ".json").toFile();
        outputFile.deleteOnExit();

        App validApp = new App();
        new CommandLine(validApp).parseArgs("-i", dummyLuaFile.getAbsolutePath(), "-o",
                outputFile.getAbsolutePath());
        validApp.run();

        assertTrue(outputFile.exists());
        String content = Files.readString(outputFile.toPath());
        assertTrue(content.contains("\"MAGIC\""));
    }

    @Test
    void testApp_generatesValidOutput() throws Exception {
        File outputFile = Files.createTempFile("generate-output", ".json").toFile();
        outputFile.deleteOnExit();

        App outputApp = new App();
        new CommandLine(outputApp).parseArgs("-i", dummyLuaFile.getAbsolutePath(), "-o",
                outputFile.getAbsolutePath());
        outputApp.run();

        String content = Files.readString(outputFile.toPath());
        assertTrue(content.trim().startsWith("{"));
        assertTrue(content.trim().endsWith("}"));
    }

    @Test
    void testApp_outputFormat_JSON() throws Exception {
        File outputFile = Files.createTempFile("json-output", ".json").toFile();
        outputFile.deleteOnExit();

        App jsonApp = new App();
        new CommandLine(jsonApp).parseArgs("-i", dummyLuaFile.getAbsolutePath(), "-o",
                outputFile.getAbsolutePath());
        jsonApp.run();

        assertTrue(outputFile.exists());
        Map<?, ?> parsed = new ObjectMapper().readValue(outputFile, Map.class);
        assertNotNull(parsed);
        assertTrue(parsed.containsKey("MAGIC"));
    }

    @Test
    void testApp_outputFormat_YAML() throws Exception {
        File outputFile = Files.createTempFile("yaml-output", ".yaml").toFile();
        outputFile.deleteOnExit();

        App yamlApp = new App();
        new CommandLine(yamlApp).parseArgs("-i", dummyLuaFile.getAbsolutePath(), "-o",
                outputFile.getAbsolutePath());
        yamlApp.run();

        assertTrue(outputFile.exists());
        Map<?, ?> parsed = new ObjectMapper(new YAMLFactory()).readValue(outputFile, Map.class);
        assertNotNull(parsed);
        assertTrue(parsed.containsKey("MAGIC"));
    }

    @Test
    void testApp_withSpecFile() throws Exception {
        String specContent = "- operation: shift\n  spec:\n    MAGIC: magic\n";
        Path specPath = Files.createTempFile("output-spec", ".yaml");
        Files.writeString(specPath, specContent);
        specPath.toFile().deleteOnExit();

        File outputFile = Files.createTempFile("spec-file-output", ".json").toFile();
        outputFile.deleteOnExit();

        App specApp = new App();
        new CommandLine(specApp).parseArgs("-i", dummyLuaFile.getAbsolutePath(), "-o",
                outputFile.getAbsolutePath(), "-s", specPath.toAbsolutePath().toString());
        specApp.run();

        String content = Files.readString(outputFile.toPath());
        assertTrue(content.contains("\"magic\""));
    }

    @Test
    void testApp_handlesMissingInputFile() throws Exception {
        File outputFile = Files.createTempFile("missing-input-output", ".json").toFile();
        outputFile.deleteOnExit();

        App missingInputApp = new App();
        new CommandLine(missingInputApp).parseArgs("-i", "/tmp/nonexistent-file.lua", "-o",
                outputFile.getAbsolutePath());
        assertThrows(Exception.class, missingInputApp::run);
    }

    @Test
    void testApp_createsOutputFile() throws Exception {
        File outputFile = new File(Files.createTempDirectory("create-output-dir").toFile(),
                "created-output.json");
        outputFile.deleteOnExit();

        App createApp = new App();
        new CommandLine(createApp).parseArgs("-i", dummyLuaFile.getAbsolutePath(), "-o",
                outputFile.getAbsolutePath());
        createApp.run();

        assertTrue(outputFile.exists());
    }

    private File getResourceFile(String resourcePath) throws Exception {
        URL resource = this.getClass().getResource(resourcePath);
        assertNotNull(resource, "Resource not found: " + resourcePath);
        return Path.of(resource.toURI()).toFile();
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}
