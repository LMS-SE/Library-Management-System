package edu.software.lms;

import java.util.Objects;

/**
 * Simple generic pair type used for returning two values together
 * (such as success flag + message).
 *
 * @param <A> type of first element
 * @param <B> type of second element
 */
public final class Pair<A, B> {

    /** First stored value. */
    A first;

    /** Second stored value. */
    B second;

    /**
     * Constructs a new pair.
     *
     * @param first  first value
     * @param second second value
     */
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first)
                && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(first);
        result = 31 * result + Objects.hashCode(second);
        return result;
    }
}
