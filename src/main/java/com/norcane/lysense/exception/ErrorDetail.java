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
package com.norcane.lysense.exception;

import java.util.ArrayList;
import java.util.List;

import static com.norcane.toolkit.Prelude.nonNull;

public record ErrorDetail(String problem, String solution, List<String> seeAlsoLinks) {

    public static Builder.ProblemStep builder() {
        return problem -> solution -> new Builder.FinalStep(problem, solution);
    }

    public static final class Builder {

        @FunctionalInterface
        public interface ProblemStep {
            SolutionStep problem(String problem);
        }

        @FunctionalInterface
        public interface SolutionStep {
            FinalStep solution(String solution);
        }

        public static final class FinalStep {
            private final String problem;
            private final String solution;
            private final List<String> seeAlsoLinks = new ArrayList<>();

            public FinalStep(String problem, String solution) {
                this.problem = nonNull(problem);
                this.solution = nonNull(solution);
            }

            public FinalStep seeAlsoLink(String seeAlsoLink) {
                seeAlsoLinks.add(seeAlsoLink);
                return this;
            }

            public ErrorDetail build() {
                return new ErrorDetail(problem, solution, seeAlsoLinks);
            }
        }
    }
}
