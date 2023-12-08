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
