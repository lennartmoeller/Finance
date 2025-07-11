package com.lennartmoeller.finance.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;

class ImmutablePairUtilsTest {
    @Test
    void crossProductStreamReturnsPairs() {
        List<Integer> left = List.of(1, 2);
        List<String> right = List.of("a", "b");

        List<ImmutablePair<Integer, String>> result =
                ImmutablePairUtils.crossProductStream(left, right).collect(Collectors.toList());

        assertThat(result)
                .containsExactlyInAnyOrder(
                        new ImmutablePair<>(1, "a"),
                        new ImmutablePair<>(1, "b"),
                        new ImmutablePair<>(2, "a"),
                        new ImmutablePair<>(2, "b"));
    }

    @Test
    void crossProductStreamHandlesNullOrEmpty() {
        assertThat(ImmutablePairUtils.crossProductStream(null, List.of("a")).count()).isZero();
        assertThat(ImmutablePairUtils.crossProductStream(List.of(1), null).count()).isZero();
        assertThat(ImmutablePairUtils.crossProductStream(List.of(), List.of(1)).count()).isZero();
    }
}
