package io.github.jenrsparks.hades;

import static io.github.jenrsparks.hades.constants.WriterConstant.PASSTHROUGH_SPEC_FILE;
import java.io.File;
import java.util.Map;
import org.slf4j.LoggerFactory;
import io.github.jenrsparks.hades.actors.HadesConverter;
import io.github.jenrsparks.hades.actors.HadesDataTranslator;
import io.github.jenrsparks.hades.actors.HadesWriter;
import io.github.jenrsparks.hades.actors.LuaDataExtractor;
import io.github.jenrsparks.hades.helpers.FileHelper;
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
            description = "Translation language code (e.g. 'en', 'es')", required = false)
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
        // applyTranslation(rawData, language, translationFile);
        convertAndWrite(translatedData, outputFile, specFile, PASSTHROUGH_SPEC_FILE.getValue());
    }

    // TODO All this should be encapsulated in a separate translation layer
    boolean applyTranslation(Map<String, Object> data, String langCode, File transFile) {
        if (transFile != null) {
            logger.info("Custom translation file provided: " + transFile.getPath());
        } else if (langCode != null) {
            logger.info("Translation language selected: " + langCode);
        } else {
            logger.info("No translation language provided; using default (en)");
        }
        String resolvedLangCode = (langCode != null) ? langCode : "en";

        return true;
    }

    // TODO This should be encapsulated in a separate conversion layer
    private boolean convertAndWrite(Map<String, Object> data, File target, File specFile,
            String defaultSpecPath) {
        FileFormat outputFormat = FileFormat.getFileFormat(target);
        specFile = FileHelper.getFileWithFallbackResource(specFile, defaultSpecPath);
        Map<String, Object> convertedData = new HadesConverter(specFile).convert(data);
        boolean success =
                HadesWriter.getInstance(outputFormat, convertedData).target(target).write();
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

}
