package io.github.jenrsparks.hades.actors.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.jenrsparks.hades.actors.HadesWriter;

public class HadesYamlWriter extends HadesWriter {

    private Map<String,Object> data;
    private File targetFile;

    public HadesYamlWriter(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public HadesWriter target(File outputFile) {
        this.targetFile = outputFile;
        return this;
    }

    @Override
    public boolean write() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            mapper.writeValue(targetFile, data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write YAML to file", e);
        }
        
        return true;
    }

}
