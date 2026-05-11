package io.github.jenrsparks.hades.actors;

import java.io.File;
import io.github.jenrsparks.hades.FileFormat;
import io.github.jenrsparks.hades.actors.impl.HadesJsonWriter;
import io.github.jenrsparks.hades.actors.impl.HadesYamlWriter;

public abstract class HadesWriter {

    protected HadesWriter() {
        // intentionally empty - cannot be instantiated directly, only through getInstance() factory method
    }

    public static HadesWriter getInstance(File targetFile) {
        FileFormat outputFormat = FileFormat.getFileFormat(targetFile);
        switch(outputFormat) {
            case FileFormat.JSON:
                // For now, we only have JSON output, but this is where we would add support for other formats like YAML in the future
                return new HadesJsonWriter().target(targetFile);
            case FileFormat.YAML:
                return new HadesYamlWriter().target(targetFile);
            default:
                throw new IllegalArgumentException("Unsupported output format: " + outputFormat);
        }
    }

    public abstract HadesWriter data(java.util.Map<String, Object> data);
    protected abstract HadesWriter target(File outputFile);
    public abstract boolean write();

}
