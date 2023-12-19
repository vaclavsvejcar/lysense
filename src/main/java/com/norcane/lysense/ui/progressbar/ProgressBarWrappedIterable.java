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
package com.norcane.lysense.ui.progressbar;


import com.norcane.lysense.ui.console.Console;

import java.util.Iterator;
import java.util.function.Function;

import jakarta.annotation.Nonnull;

import static com.norcane.toolkit.Prelude.nonNull;


/**
 * Wrapper over existing {@link Iterable} that automatically tracks the progress and properly updates the underlying {@link ProgressBar}. This is useful to be
 * used together with for example <i>enhanced for loops</i>.
 *
 * @param <T> type of iterator items
 */
public class ProgressBarWrappedIterable<T> implements Iterable<T> {

    private final Iterator<T> underlying;
    private final ProgressBar progressBar;
    private final Function<T, String> messageFn;
    private final Console console;

    /**
     * Constructs new instance for given {@link Iterable} and {@link ProgressBar}.
     *
     * @param iterable    iterable to wrap
     * @param progressBar progress bar used to track the progress
     * @param messageFn   produces message displayed by progress bar for every item of {@link Iterable}
     * @param console     console used to render the progress bar
     */
    public ProgressBarWrappedIterable(Iterable<T> iterable,
                                      ProgressBar progressBar,
                                      Function<T, String> messageFn,
                                      Console console) {

        this.underlying = nonNull(iterable).iterator();
        this.progressBar = nonNull(progressBar);
        this.messageFn = nonNull(messageFn);
        this.console = nonNull(console);
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                final boolean hasNext = underlying.hasNext();

                if (!hasNext) {
                    progressBar.cleanup(console);
                }

                return hasNext;
            }

            @Override
            public T next() {
                final T next = underlying.next();

                progressBar.step(messageFn.apply(next));
                progressBar.render(console);

                return next;
            }
        };
    }
}
