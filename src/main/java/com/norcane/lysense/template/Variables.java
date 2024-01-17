/*
 * lysense :: license header manager
 * Copyright (c) 2023-2024 Vaclav Svejcar
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.norcane.lysense.template;

import com.google.common.base.MoreObjects;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * Merges current variables with other ones. If both instances contain same variable names, latter one will be selected.
     *
     * @param other instance to merge with
     * @return new instance with merged variables
     */
    public Variables mergeWith(Variables other) {
        final Map<String, Object> mergedVariables = Stream.concat(variables.entrySet().stream(), other.variables.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (_, v2) -> v2));

        return Variables.from(mergedVariables);
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
     * @param name  name of the variable
     * @param value value of the variable
     */
    public record Variable(String name, Object value) {
    }
}
