package io.github.jenrsparks.hades.actors;

import java.io.File;
import java.util.Map;
import io.github.jenrsparks.hades.FileFormat;
import io.github.jenrsparks.hades.actors.impl.HadesJsonWriter;
import io.github.jenrsparks.hades.actors.impl.HadesYamlWriter;

public abstract class HadesWriter {

    protected HadesWriter() {
        // intentionally empty - cannot be instantiated directly, only through getInstance() factory method
    }

    public static HadesWriter getInstance(FileFormat outputFormat, Map<String, Object> data) {
        switch(outputFormat) {
            case FileFormat.JSON:
                // For now, we only have JSON output, but this is where we would add support for other formats like YAML in the future
                return new HadesJsonWriter(data);
            case FileFormat.YAML:
                return new HadesYamlWriter(data);
            default:
                throw new IllegalArgumentException("Unsupported output format: " + outputFormat);
        }
    }

    public abstract HadesWriter target(File outputFile);
    public abstract boolean write();

}
