package com.github.vincentrussell.validation.testClasses.defaultValidators;

import com.github.vincentrussell.validation.annotation.Max;
import com.github.vincentrussell.validation.annotation.NotNull;
import com.github.vincentrussell.validation.annotation.Size;

public class SimpleObjectDefaultValidators {

    @NotNull(errorMessage = "you must provide a value for field1")
    private String field1;

    @Size(errorMessage = "field is not in within size constraints", min = 1, max = 10)
    private String field2;

    @Max(32)
    private long field3;

    public String getField1() {
        return field1;
    }

    public SimpleObjectDefaultValidators setField1(String field1) {
        this.field1 = field1;
        return this;
    }

    public String getField2() {
        return field2;
    }

    public SimpleObjectDefaultValidators setField2(String field2) {
        this.field2 = field2;
        return this;
    }

    public long getField3() {
        return field3;
    }

    public SimpleObjectDefaultValidators setField3(long field3) {
        this.field3 = field3;
        return this;
    }
}
