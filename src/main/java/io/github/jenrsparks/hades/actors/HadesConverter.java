package io.github.jenrsparks.hades.actors;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.Transform;

public class HadesConverter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Transform transformer;

    public HadesConverter(File joltSpecYaml) {

        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        try {
            @SuppressWarnings("unchecked")
            List<Object> spec = yamlMapper.readValue(joltSpecYaml, List.class);
            this.transformer = Chainr.fromSpec(spec);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Jolt spec: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> convert(Map<String, Object> data) {
        if (transformer == null) {
            return data;
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
