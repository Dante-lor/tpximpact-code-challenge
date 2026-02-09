package com.tpximpact.shortenerservice.model;

import java.util.Collections;
import java.util.List;

/**
 * Validation result used internally to collate errors and decide if a request is valid.
 * No errors = valid.
 *
 * @param errors the errors that the request generated.
 */
public record ValidationResult(List<String> errors) {

    public ValidationResult() {
        this(Collections.emptyList());
    }

    public boolean isValid() {
        return errors.isEmpty();
    }
    
}
