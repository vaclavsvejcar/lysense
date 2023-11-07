package com.norcane.toolkit.state;

import com.google.common.base.MoreObjects;

import java.util.Optional;
import java.util.function.Supplier;

import static com.norcane.toolkit.Prelude.nonNullOrDefault;

/**
 * Represents variable which value is computed only once using given supplier and then memoized.
 *
 * @param <T> type of the memoized value
 */
public final class Memoized<T> implements Stateful {

    private T value;

    private Memoized(T value) {
        this.value = value;
    }

    /**
     * Constructs new instance with no memoized value.
     *
     * @param <T> type of the memoized value
     * @return new instance
     */
    public static <T> Memoized<T> empty() {
        return new Memoized<>(null);
    }

    /**
     * Constructs new instance with no memoized value and registers it to given <i>stateful</i> parent, so when the parent state is reset, the memoized value is
     * reset as well.
     *
     * @param parent parent stateful object
     * @param <T> type of the memoized value
     * @return new instance
     */
    public static <T> Memoized<T> bindTo(Stateful parent) {
        final Memoized<T> memoized = new Memoized<>(null);

        parent.stateContext().register(memoized);
        return memoized;
    }

    /**
     * Returns the memoized value and if no value has been computed yet, uses given supplier to compute and store the value.
     *
     * @param supplier supplier used to compute the value
     * @return value
     */
    public T computeIfAbsent(Supplier<T> supplier) {
        return value == null
               ? (value = supplier.get())
               : value;
    }

    /**
     * Returns the memoized value (if computed) or empty optional.
     *
     * @return memoized value
     */
    public Optional<T> get() {
        return Optional.ofNullable(value);
    }

    /**
     * Returns whether if the memoized value is present
     *
     * @return {@code true} if the memoized value is present
     */
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public void resetState() {
        value = null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("value", nonNullOrDefault(value, "<absent>"))
            .toString();
    }
}
