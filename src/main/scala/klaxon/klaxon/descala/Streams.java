package klaxon.klaxon.descala;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Streams {

    /**
     * Like Java's reduce, but it need not return the same type as the collection.
     * Should be equivalent to Scala's foldLeft
     */
    public static <T, U> T foldLeft(T start, Collection<U> coll, BiFunction<T, U, T> func) {

        for (U elem : coll) {
            start = func.apply(start, elem);
        }
        return start;
    }
}
