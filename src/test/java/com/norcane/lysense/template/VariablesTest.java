package com.norcane.lysense.template;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class VariablesTest {

    @Test
    void from_listOfVariable() {
        final Variables variables = Variables.from(List.of(new Variables.Variable("foo", "bar")));

        assertEquals(Map.of("foo", "bar"), variables.toMap());
    }

    @Test
    void toMap() {
        final Map<String, Object> raw = Map.of("one", 1, "two", 2);
        final Variables variables = Variables.from(raw);

        assertEquals(raw, variables.toMap());
    }

    @Test
    void isEmpty() {
        assertTrue(Variables.empty().isEmpty());
        assertFalse(Variables.from(Map.of("one", 1)).isEmpty());
    }

    @Test
    void size() {
        assertEquals(0, Variables.empty().size());
        assertEquals(1, Variables.from(Map.of("one", 1)).size());
    }

    @Test
    void testEquals() {
        assertEquals(Variables.empty(), Variables.empty());
        assertNotEquals(Variables.empty(), "other type");
        assertNotEquals(null, Variables.from(Map.of()));
    }

    @Test
    void testHashCode() {
        final Variables variables1 = Variables.from(Map.of("one", 1));
        final Variables variables2 = Variables.from(Map.of("two", 2));

        assertEquals(variables1.hashCode(), variables1.hashCode());
        assertNotEquals(variables1.hashCode(), variables2.hashCode());
    }

    @Test
    void testToString() {
        assertNotNull(Variables.from(Map.of("one", 1)).toString());
    }
}