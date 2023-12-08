package com.norcane.lysense.source.variables;

import com.norcane.lysense.template.Variables;

import java.util.Optional;

/**
 * Extracts {@link Variables} from analyzed source code that will be later used for license header templates pattern variables.
 */
public interface VariablesExtractor {

    /**
     * Extracts variable from given line of source code.
     *
     * @param line line of source code to extract from
     * @return extracted variable (if present)
     */
    Optional<Variables.Variable> extract(String line);

    /**
     * Instance of {@link VariablesExtractor} that doesn't extract anything.
     *
     * @return instance
     */
    static VariablesExtractor noExtraction() {
        return _ -> Optional.empty();
    }
}
