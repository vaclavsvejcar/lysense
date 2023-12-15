package com.norcane.lysense.configuration.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.norcane.lysense.template.Variables;

import java.io.IOException;
import java.util.Map;

/**
 * Deserializer for {@link Variables}.
 */
public class VariablesDeserializer extends JsonDeserializer<Variables> {

    @Override
    public Variables deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final Map<String, Object> variablesMap = mapper.readValue(jsonParser, new TypeReference<>() {});

        return Variables.from(variablesMap);
    }
}
