package io.github.jenrsparks.hades.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

public class WriterConstantTest {
    @Test
    void testGetValue() {

        WriterConstant constant = WriterConstant.PASSTHROUGH_SPEC_FILE;
        Object constantValue = constant.getValue();
        assertEquals(constantValue.getClass(), String.class);
        assert (StringUtils.isNotBlank((String) constantValue));
        // Skipping checking of the literal value as that's left up to
        // the enum definition and not the getValue() method
    }

    @Test
    void testValueOf_match() {
        WriterConstant constant = WriterConstant.valueOf("PASSTHROUGH_SPEC_FILE");
        assertEquals(constant, WriterConstant.PASSTHROUGH_SPEC_FILE);
    }

    @Test
    void testValueOf_noMatch() {
        assertThrows(IllegalArgumentException.class, () -> {
            WriterConstant.valueOf("DUMMY");
        });
    }

    @Test
    void testValues() {
        WriterConstant[] constants = WriterConstant.values();
        assert(constants.length > 0);
        WriterConstant found = null;
        for(WriterConstant constant : constants) {
            assertNotNull(constant);
            if(constant == WriterConstant.PASSTHROUGH_SPEC_FILE) {
                found = constant;
                break;
            }
        }
        assertNotNull(found);
        assertEquals(found, WriterConstant.PASSTHROUGH_SPEC_FILE);
    }
}
