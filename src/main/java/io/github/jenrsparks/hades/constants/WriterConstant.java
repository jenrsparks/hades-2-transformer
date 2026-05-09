package io.github.jenrsparks.hades.constants;

public enum WriterConstant {

    PASSTHROUGH_SPEC_FILE("/passthrough-spec.yaml"),
    ;

    private final String fieldName;

    WriterConstant(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return fieldName;
    }

}
