package io.github.jenrsparks.hades.actors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

public class HadesWriter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    FileWriter fileWriter;

    public HadesWriter(File outputFile) {
        try {
            this.fileWriter = new FileWriter(outputFile);
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to create FileWriter for file: " + outputFile.getAbsolutePath(), e);
        }
    }

    public boolean writeJsonToFile(JsonObject data) {
        boolean success = false;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        try {
            gson.toJson(data, fileWriter);
            fileWriter.flush();
            success = true;
        } catch (JsonIOException e) {
            throw new RuntimeException("Failed to write JSON to file", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to flush FileWriter", e);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                // Log the error but don't rethrow since the main operation succeeded
                logger.warn("Failed to close FileWriter: " + e.getMessage());
            }
        }
        return success;
    }

}
