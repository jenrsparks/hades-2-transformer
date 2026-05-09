package io.github.jenrsparks.hades;

import static io.github.jenrsparks.hades.constants.WriterConstant.PASSTHROUGH_SPEC_FILE;
import java.io.File;
import java.net.URL;
import java.util.Map;
import org.slf4j.LoggerFactory;
import io.github.jenrsparks.FileFormat;
import io.github.jenrsparks.hades.actors.HadesConverter;
import io.github.jenrsparks.hades.actors.HadesWriter;
import io.github.jenrsparks.hades.actors.LuaDataExtractor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import org.slf4j.Logger;

@Command(name = "hades-2-transformer", mixinStandardHelpOptions = true, version = "1.0",
        description = "Converts Hades 2 LUA files to another format with transformations & dictionaries applied.")
public class App implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 
     * Planned features:
     * - TODO - Translation file language selection
     * - TODO - Custom translation file as alternative
     * 
     */

    @Option(names = {"-i", "--input"}, description = "Input LUA file", defaultValue = "save2.lua")
    private File inputFile;

    @Option(names = {"-o", "--output"}, description = "Output file name",
            defaultValue = "save2.json")
    private File outputFile;

    @Option(names = {"-s", "--spec"}, description = "JOLT spec JSON file for data transformation",
            required = false)
    private File specFile;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        Map<String, Object> rawData = new LuaDataExtractor().extract(inputFile);
        convertAndWrite(rawData, outputFile, specFile, PASSTHROUGH_SPEC_FILE.getValue());
    }

    private boolean convertAndWrite(Map<String, Object> data, File target, File specFile, String defaultSpecPath) {
        FileFormat outputFormat = FileFormat.getFileFormat(target);
        specFile = getOrDefaultSpecFile(specFile, defaultSpecPath);
        Map<String, Object> convertedData = new HadesConverter(specFile).convert(data);
        boolean success = HadesWriter.getInstance(outputFormat, convertedData).target(target).write();
        reportWriteResult(success, target);
        return success;
    }

    boolean reportWriteResult(boolean success, File target) {
        if (success) {
            logger.debug("Successfully wrote JSON to file: " + target.getAbsolutePath());
        } else {
            logger.error("Failed to write JSON to file: " + target.getAbsolutePath());
        }
        return success;
    }

    File getOrDefaultSpecFile(File specFile, String defaultSpecPath) {
        if (specFile != null && specFile.exists() && specFile.isFile()) {
            return specFile;
        }

        if (specFile != null) {
            logger.warn("Specified spec file '{}' does not exist or is not a regular file. Falling back to default specification.",
                    specFile.getAbsolutePath());
        }

        URL resource = this.getClass().getResource(defaultSpecPath);
        if (resource == null) {
            throw new IllegalStateException("Default spec resource not found: " + defaultSpecPath);
        } else {
            return new File(resource.getFile());
        }
    }
}
