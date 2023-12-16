package com.norcane.lysense.source;

import com.norcane.toolkit.ValueClass;

/**
 * Type-safe wrapper for <i>language id</i>, which is a string that uniquely identifies programming language.
 *
 * @param value string representation of the language id
 */
public record LanguageId(String value) implements ValueClass<String> {

    /**
     * Creates new instance of {@link LanguageId}.
     *
     * @param value string representation of the language id
     * @return new instance of {@link LanguageId}
     */
    public static LanguageId languageId(String value) {
        return new LanguageId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
