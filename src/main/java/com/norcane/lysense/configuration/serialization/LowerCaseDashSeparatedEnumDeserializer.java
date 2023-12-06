package com.norcane.lysense.configuration.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Deserializer for enums that are represented as lower-case dash-separated strings.
 *
 * @param <T> enum type
 */
public class LowerCaseDashSeparatedEnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> {
    private final Class<T> enumClass;

    private LowerCaseDashSeparatedEnumDeserializer(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    /**
     * Constructs a new instance of {@link LowerCaseDashSeparatedEnumDeserializer} for the given enum class.
     *
     * @param enumClass enum class
     * @param <T> enum type
     * @return new instance of {@link LowerCaseDashSeparatedEnumDeserializer}
     */
    public static <T extends Enum<T>> LowerCaseDashSeparatedEnumDeserializer<T> forEnum(Class<T> enumClass) {
        return new LowerCaseDashSeparatedEnumDeserializer<>(enumClass);
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {

        final String value = jsonParser.getText();
        final String upperCaseValue = value.replace('-', '_').toUpperCase();

        return Enum.valueOf(enumClass, upperCaseValue);
    }
}
