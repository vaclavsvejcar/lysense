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
package com.norcane.lysense.source.variables;

import com.norcane.lysense.template.Variables;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.norcane.toolkit.Prelude.nonNull;

/**
 * Implementation of {@link VariablesExtractor} that uses <i>regular expressions</i> to identify and extract {@link Variables} from source code.
 */
public class PatternVariablesExtractor implements VariablesExtractor {

    private final Map<String, Pattern> patterns;

    private PatternVariablesExtractor(Map<String, Pattern> patterns) {
        this.patterns = patterns;
    }

    /**
     * Creates new instance with given map of patterns, where key is the name of the <i>dynamic variable</i>> and value is the pattern to extract it.
     *
     * @param patterns map of patterns
     * @return new instance
     */
    public static PatternVariablesExtractor from(Map<String, Pattern> patterns) {
        return new PatternVariablesExtractor(nonNull(patterns));
    }

    @Override
    public Optional<Variables.Variable> extract(String line) {
        for (final String patternKey : patterns.keySet()) {
            final Matcher matcher = patterns.get(patternKey).matcher(line);

            if (matcher.find()) {
                return Optional.of(new Variables.Variable(patternKey, matcher.group(1)));
            }
        }

        return Optional.empty();
    }
}
