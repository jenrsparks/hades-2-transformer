package io.github.jenrsparks.hades.constants;

public enum WriterConstant {

    DEFAULT_SPEC_FILE("/default-jolt-spec.yaml"),
    PASSTHROUGH_SPEC_FILE("/passthrough-spec.yaml")
    ;

    private final String fieldName;

    WriterConstant(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return fieldName;
    }


}
