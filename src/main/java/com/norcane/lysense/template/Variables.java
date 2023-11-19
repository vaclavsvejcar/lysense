package com.norcane.lysense.template;

import com.google.common.base.MoreObjects;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Represents a collection of actual template variables (as {@code placeholder -> value}) that will be used to fill in placeholders in rendered template.
 */
public final class Variables {

    private static final Variables EMPTY = Variables.from(Map.of());

    private final Map<String, Object> variables;

    private Variables(Map<String, Object> variables) {
        this.variables = variables;
    }

    /**
     * Constructs new instance using the given map as a source of {@code placeholder -> value} variable definitions.
     *
     * @param variables map of variables
     * @return constructed instance
     */
    public static Variables from(Map<String, Object> variables) {
        nonNull(variables);

        return new Variables(variables);
    }

    /**
     * Constructs new instance using the given list of {@link Variable} as a source of {@code placeholder -> value} variable definitions.
     *
     * @param variables list of variables
     * @return constructed instance
     */
    public static Variables from(List<Variable> variables) {
        nonNull(variables);

        return new Variables(variables.stream().collect(Collectors.toMap(Variable::name, Variable::value)));
    }

    /**
     * Returns instance with no variables. Prefer this over {@code Variables.of(Map.of())} as this reuses single static instance.
     *
     * @return instance with no variables
     */
    public static Variables empty() {
        return EMPTY;
    }

    /**
     * Returns whether this instance contains no variables.
     *
     * @return {@code true} if this instance contains no variables
     */
    public boolean isEmpty() {
        return variables.isEmpty();
    }

    /**
     * Returns readonly map representation of variables as {@code placeholder -> value} relation.
     *
     * @return map representation
     */
    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(variables);
    }

    /**
     * Returns number of variables.
     *
     * @return number of variables
     */
    public int size() {
        return variables.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Variables variables1 = (Variables) o;
        return Objects.equals(variables, variables1.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("variables", variables)
            .toString();
    }

    /**
     * Represents single variable (as a pair of its name and value)
     *
     * @param name name of the variable
     * @param value value of the variable
     */
    public record Variable(String name, Object value) {
    }
}
