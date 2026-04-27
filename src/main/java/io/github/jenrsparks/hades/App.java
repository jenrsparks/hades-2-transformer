package io.github.jenrsparks.hades;

import java.io.File;
import java.util.Map;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;
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

    @Option(names = {"-o", "--output"}, description = "Output JSON file", defaultValue = "save2.json")
    private File outputFile;

    
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
    
    @Override
    public void run() {
        logger.debug("Reading from: " + inputFile.getAbsolutePath());
        logger.debug("Writing to: " + outputFile.getAbsolutePath());
        
        Map<String,Object> luaData = new LuaDataExtractor().extract(inputFile);        
        JsonObject data = new HadesConverter().convert(luaData);

        boolean success = new HadesWriter(outputFile).writeJsonToFile(data);
        if (success) {
            logger.debug("Successfully wrote JSON to file: " + outputFile.getAbsolutePath());
        } else {
            logger.error("Failed to write JSON to file: " + outputFile.getAbsolutePath());
        }
    }
}
