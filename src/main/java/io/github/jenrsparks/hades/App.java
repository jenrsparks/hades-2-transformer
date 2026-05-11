package io.github.jenrsparks.hades;

import static io.github.jenrsparks.hades.constants.WriterConstant.PASSTHROUGH_SPEC_FILE;
import java.io.File;
import java.util.Map;
import org.slf4j.LoggerFactory;
import io.github.jenrsparks.hades.actors.HadesConverter;
import io.github.jenrsparks.hades.actors.HadesDataTranslator;
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

    @Option(names = {"-i", "--input"}, description = "Input LUA file", defaultValue = "save1.lua")
    private File inputFile;

    @Option(names = {"-o", "--output"}, description = "Output file name",
            defaultValue = "save1.json")
    private File outputFile;

    @Option(names = {"-s", "--spec"}, description = "JOLT spec JSON file for data transformation",
            required = false)
    private File specFile;

    @Option(names = {"-l", "--language"},
            description = "Translation language code (e.g. 'en', 'es')", required = false, defaultValue = "en")
    private String language;

    @Option(names = {"-t", "--translation-file"}, description = "Custom translation file path",
            required = false)
    private File translationFile;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        Map<String, Object> rawData = new LuaDataExtractor().extract(inputFile);

        Map<String, Object> translatedData = //
                HadesDataTranslator.getInstance() //
                        .withLanguage(language) //
                        .withDictionaryFile(translationFile) //
                        .translate(rawData);

        Map<String, Object> convertedData = //
                HadesConverter.getInstance() //
                        .withSpec(specFile, PASSTHROUGH_SPEC_FILE.getValue()) //
                        .convert(translatedData); //

        boolean success = HadesWriter.getInstance(outputFile).data(convertedData).write();

        reportWriteResult(success, outputFile);
    }

    boolean reportWriteResult(boolean success, File target) {
        if (success) {
            logger.debug("Successfully wrote JSON to file: " + target.getAbsolutePath());
        } else {
            logger.error("Failed to write JSON to file: " + target.getAbsolutePath());
        }
        return success;
    }

}
