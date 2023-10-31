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
