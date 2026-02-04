package com.tpximpact.shortenerservice.model;

import java.util.Collections;
import java.util.List;

public record ValidationResult(List<String> errors) {

    public ValidationResult() {
        this(Collections.emptyList());
    }

    public boolean isValid() {
        return errors.isEmpty();
    }
    
}
