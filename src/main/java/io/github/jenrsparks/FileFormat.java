package io.github.jenrsparks;

public enum FileFormat {
    JSON("json"),
    YAML("yaml")
    ;

    private final String formatName;

    FileFormat(String formatName) {
        this.formatName = formatName;
    }

    public String getValue() {
        return formatName;
    }
}
