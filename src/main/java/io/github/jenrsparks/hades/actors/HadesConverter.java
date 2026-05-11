package io.github.jenrsparks.hades.actors;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.jenrsparks.hades.helpers.FileHelper;
import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.Transform;

public class HadesConverter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Transform transformer;
    private ObjectMapper yamlMapper;


    private HadesConverter() {
        yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    public static HadesConverter getInstance() {
        return new HadesConverter();
    }

    public HadesConverter withSpec(File specFile, String defaultSpecPath) {
        // Fallback to default spec resource if not provided or invalid
        specFile = FileHelper.getFileWithFallbackResource(specFile, defaultSpecPath);

        try {
            List<?> spec = yamlMapper.readValue(specFile, List.class);
            this.transformer = Chainr.fromSpec(spec);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse specification; " + e.getMessage(), e);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> convert(Map<String, Object> data) {
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer not initialized; call withSpec() to set the transformation specification.");
        }
        if(data == null) {
            logger.warn("Input data is null; treating as empty data.");
            return new HashMap<>();
        }
        
        Object result = transformer.transform(data);
        if (result instanceof Map) {
            return (Map<String, Object>) result;
        } else {
            logger.error("Jolt transformation did not return a Map. Returning empty data.");
            return new HashMap<>();
        }
    }

}
