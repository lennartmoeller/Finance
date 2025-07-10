package com.lennartmoeller.finance.util;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.ImmutablePair;

public final class ImmutablePairUtils {
    private ImmutablePairUtils() {}

    public static <T, U> Stream<ImmutablePair<T, U>> crossProductStream(
            @Nullable List<? extends T> list1, @Nullable List<? extends U> list2) {
        if (list1 == null || list1.isEmpty() || list2 == null || list2.isEmpty()) {
            return Stream.of();
        }
        return list1.stream().flatMap(a -> list2.stream().map(b -> new ImmutablePair<>(a, b)));
    }
}
