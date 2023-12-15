package com.norcane.toolkit;

/**
 * Represents generic factory capable of constructing new instance of type {@link T}.
 *
 * @param <T> type of the constructed object
 */
public interface InstanceFactory<T> {

    /**
     * Constructs new instance of the class.
     *
     * @return new instance
     */
    T instance();
}
