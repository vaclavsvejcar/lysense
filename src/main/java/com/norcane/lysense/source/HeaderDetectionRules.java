package com.norcane.lysense.source;

import java.util.regex.Pattern;

import static com.norcane.toolkit.Prelude.nonNull;


public class HeaderDetectionRules {

    private static final HeaderDetectionRules INSTANCE_NO_RULES = new HeaderDetectionRules(null, null);

    private final Pattern putAfter;
    private final Pattern putBefore;

    private HeaderDetectionRules(Pattern putAfter, Pattern putBefore) {
        this.putAfter = putAfter;
        this.putBefore = putBefore;
    }

    public static HeaderDetectionRules from(Pattern putAfter, Pattern putBefore) {
        return new HeaderDetectionRules(nonNull(putAfter), nonNull(putBefore));
    }

    public static HeaderDetectionRules putAfter(Pattern putAfter) {
        return new HeaderDetectionRules(nonNull(putAfter), null);
    }

    public static HeaderDetectionRules putBefore(Pattern putBefore) {
        return new HeaderDetectionRules(null, nonNull(putBefore));
    }

    public static HeaderDetectionRules noRules() {
        return INSTANCE_NO_RULES;
    }

    public boolean isPutAfter(String input) {
        nonNull(input);

        return putAfter != null && putAfter.matcher(input).find();
    }

    public boolean isPutBefore(String input) {
        nonNull(input);

        return putBefore != null && putBefore.matcher(input).find();
    }
}
