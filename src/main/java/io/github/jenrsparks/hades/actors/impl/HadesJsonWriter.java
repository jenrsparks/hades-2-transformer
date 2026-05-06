package io.github.jenrsparks.hades.actors.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import io.github.jenrsparks.hades.actors.HadesWriter;

public class HadesJsonWriter extends HadesWriter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String,Object> data;
    private FileWriter fileWriter;

    public HadesJsonWriter(Map<String, Object> data) {
        this.data = data;
    }

    public HadesWriter target(File outputFile) {
        try {
            this.fileWriter = new FileWriter(outputFile);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to create FileWriter for file: " + outputFile.getAbsolutePath(), e);
        }
        return this;
    }

    public boolean write() {
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
