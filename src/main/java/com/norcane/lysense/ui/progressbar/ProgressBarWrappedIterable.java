package com.norcane.lysense.ui.progressbar;


import com.norcane.lysense.ui.console.Console;

import java.util.Iterator;
import java.util.function.Function;

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
