package io.github.jenrsparks;

import java.io.File;
import java.util.List;

public enum FileFormat {
    JSON(List.of("json")),
    YAML(List.of("yaml", "yml"))
    ;

    private final List<String> formatNames;

    public static FileFormat getFileFormat(File file) {
        String fileName = file.getName().toLowerCase();
        for (FileFormat format : FileFormat.values()) {
            for (String formatName : format.getValues()) {
                if (fileName.endsWith("." + formatName)) {
                    return format;
                }
            }
        }
        throw new IllegalArgumentException("Unsupported file format for file: " + file.getName());
    }

    private FileFormat(List<String> formatNames) {
        this.formatNames = formatNames;
    }

    private List<String> getValues() {
        return formatNames;
    }
}
