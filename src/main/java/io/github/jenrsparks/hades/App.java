package io.github.jenrsparks.hades;

import static io.github.jenrsparks.hades.constants.WriterConstant.DEFAULT_SPEC_FILE;
import static io.github.jenrsparks.hades.constants.WriterConstant.PASSTHROUGH_SPEC_FILE;
import java.io.File;
import java.util.Map;
import javax.management.RuntimeErrorException;
import org.slf4j.LoggerFactory;
import io.github.jenrsparks.hades.actors.HadesConverter;
import io.github.jenrsparks.hades.actors.HadesWriter;
import io.github.jenrsparks.hades.actors.LuaDataExtractor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import org.slf4j.Logger;

@Command(name = "hades-2-json-converter", mixinStandardHelpOptions = true, version = "1.0",
        description = "Converts Hades 2 LUA files to JSON format.")
public class App implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Option(names = {"-i", "--input"}, description = "Input LUA file", defaultValue = "save2.lua")
    private File inputFile;

    @Option(names = {"-o", "--output"}, description = "Output JSON file",
            defaultValue = "save2.json")
    private File outputFile;

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

        // temp -- get rid of this later:
        convertAndWrite(rawData, tempFile, specFile, PASSTHROUGH_SPEC_FILE.getValue());
    }

    private boolean convertAndWrite(Map<String, Object> data, File target, File specFile, String defaultSpecPath) {
        specFile = getOrDetfaultSpecFile(specFile, defaultSpecPath);
        Map<String, Object> convertedData = new HadesConverter(specFile).convert(data);
        boolean success = new HadesWriter(convertedData).file(target).writeAsJson();

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
