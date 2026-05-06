package io.github.jenrsparks.hades;

import static io.github.jenrsparks.hades.constants.WriterConstant.DEFAULT_SPEC_FILE;
import static io.github.jenrsparks.hades.constants.WriterConstant.PASSTHROUGH_SPEC_FILE;
import java.io.File;
import java.util.Map;
import org.slf4j.LoggerFactory;
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
     * - TODO - Selection of JSON vs YAML output
     * - TODO - Translation file language selection
     * - TODO - Custom translation file as alternative
     * 
     */

    @Option(names = {"-i", "--input"}, description = "Input LUA file", defaultValue = "save2.lua")
    private File inputFile;

    @Option(names = {"-o", "--output"}, description = "Output file name",
            defaultValue = "save2.json")
    private File outputFile;

    @Option(names = {"-f", "--format"}, description = "Output format (json or yaml)", defaultValue = "json")
    private String outputFormat;

    // TODO Remove this once we have confidence in the transformation process, or at least make it a debug option only
    @Option(names = {"-t", "--temp"}, description = "Output JSON file",
            defaultValue = "save2_temp.json")
    private File tempFile;

    @Option(names = {"-s", "--spec"}, description = "JOLT spec JSON file for data transformation",
            required = false)
    private File specFile;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        logger.debug("Reading from: " + inputFile.getAbsolutePath());
        logger.debug("Writing to: " + outputFile.getAbsolutePath());

        Map<String, Object> rawData = new LuaDataExtractor().extract(inputFile);
        convertAndWrite(rawData, outputFile, specFile, DEFAULT_SPEC_FILE.getValue());

        // TODO -- add flag to use this for debug purposes
        convertAndWrite(rawData, tempFile, specFile, PASSTHROUGH_SPEC_FILE.getValue());
    }

    private boolean convertAndWrite(Map<String, Object> data, File target, File specFile, String defaultSpecPath) {
        specFile = getOrDetfaultSpecFile(specFile, defaultSpecPath);
        Map<String, Object> convertedData = new HadesConverter(specFile).convert(data);
        boolean success = HadesWriter.getInstance(outputFormat, convertedData).target(target).write();

        if (success) {
            logger.debug("Successfully wrote JSON to file: " + target.getAbsolutePath());
        } else {
            logger.error("Failed to write JSON to file: " + target.getAbsolutePath());
        }
        return success;
    }

    private File getOrDetfaultSpecFile(File specFile, String defaultSpecPath) {
        return specFile != null ? specFile
                : new File(this.getClass().getResource(defaultSpecPath).getFile());//

    }
}
