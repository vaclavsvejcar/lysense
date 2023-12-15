package com.norcane.lysense.test;

import com.norcane.lysense.configuration.api.HeaderConfig;
import com.norcane.lysense.configuration.api.HeaderSpacing;
import com.norcane.lysense.configuration.api.HeaderStyle;

public record TestHeaderConfig(HeaderStyle headerStyle) implements HeaderConfig {

        @Override
        public HeaderSpacing headerSpacing() {
            return new HeaderSpacing() {
                @Override
                public Integer blankLinesAfter() {
                    return 0;
                }

                @Override
                public Integer blankLinesBefore() {
                    return 0;
                }
            };
        }
    }