package com.norcane.lysense.ui.progressbar;

import com.google.common.collect.Iterables;

import com.norcane.lysense.ui.InteractiveUIComponent;
import com.norcane.lysense.ui.console.Console;
import com.norcane.lysense.ui.progressbar.checklist.CheckListProgressBar;
import com.norcane.lysense.ui.progressbar.concise.ConciseProgressBar;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * UI Component representing <i>progress bar</i>.
 */
public interface ProgressBar extends InteractiveUIComponent {

    /**
     * Current step of the progress bar. Can be progressed by calling {@link #step(String)}.
     *
     * @return current step
     */
    int current();

    /**
     * Progresses one step in current progress bar status, with the given message to be shown.
     *
     * @param message message to be shown
     */
    void step(String message);

    /**
     * Same as {@link #wrap} but uses the {@link ConciseProgressBar} progress bar implementation.
     *
     * @param iterable  iterable to wrap
     * @param messageFn produces message displayed by progress bar for every item of {@link Iterable}
     * @param console   console used to render the progress bar
     * @param <T>       type of the elements in iterable
     * @return iterable wrapper
     */
    static <T> ProgressBarWrappedIterable<T> concise(Iterable<T> iterable, Function<T, String> messageFn, Console console) {
        return wrap(iterable, messageFn, () -> new ConciseProgressBar(Iterables.size(iterable), "Processing..."), console);
    }

    /**
     * Same as {@link #wrap} but uses the {@link CheckListProgressBar} progress bar implementation.
     *
     * @param iterable  iterable to wrap
     * @param messageFn produces message displayed by progress bar for every item of {@link Iterable}
     * @param console   console used to render the progress bar
     * @param <T>       type of the elements in iterable
     * @return iterable wrapper
     */
    static <T> ProgressBarWrappedIterable<T> checkList(Iterable<T> iterable, Function<T, String> messageFn, Console console) {
        return wrap(iterable, messageFn, CheckListProgressBar::new, console);
    }

    /**
     * Wraps the input {@link Collection} and automatically tracks the progress and updates the underlying {@link ProgressBar}. See
     * {@link ProgressBarWrappedIterable} for more details.
     *
     * @param iterable    iterable to wrap
     * @param messageFn   produces message displayed by progress bar for every item of {@link Iterable}
     * @param progressBar instance of the {@link ProgressBar} to use
     * @param console     console used to render the progress bar
     * @param <T>         type of the elements in iterable
     * @return iterable wrapper
     */
    static <T> ProgressBarWrappedIterable<T> wrap(Iterable<T> iterable, Function<T, String> messageFn, Supplier<ProgressBar> progressBar, Console console) {
        return new ProgressBarWrappedIterable<>(iterable, progressBar.get(), messageFn, console);
    }
}
