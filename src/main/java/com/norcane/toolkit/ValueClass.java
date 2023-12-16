package com.norcane.toolkit;

/**
 * Interface representing <i>value class</i> - a class that wraps a value and provides type safety. This should be replaced by <a
 * href="https://openjdk.org/projects/valhalla/">Project Valhalla's</a> <i>value classes</i> once it will be the part of <i>Java</i>.
 */
public interface ValueClass<T> {

    /**
     * Returns wrapped value.
     *
     * @return wrapped value
     */
    T value();
}
