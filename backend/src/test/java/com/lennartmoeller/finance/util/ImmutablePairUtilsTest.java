package com.lennartmoeller.finance.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;

class ImmutablePairUtilsTest {
    @Test
    void crossProductStreamReturnsPairs() {
        List<Integer> left = List.of(1, 2);
        List<String> right = List.of("a", "b");

        List<ImmutablePair<Integer, String>> result =
                ImmutablePairUtils.crossProductStream(left, right).toList();

        assertThat(result)
                .containsExactlyInAnyOrder(
                        new ImmutablePair<>(1, "a"),
                        new ImmutablePair<>(1, "b"),
                        new ImmutablePair<>(2, "a"),
                        new ImmutablePair<>(2, "b"));
    }

    @Test
    void crossProductStreamHandlesNullOrEmpty() {
        assertThat(ImmutablePairUtils.crossProductStream(null, List.of("a")).count())
                .isZero();
        assertThat(ImmutablePairUtils.crossProductStream(List.of(1), null).count())
                .isZero();
        assertThat(ImmutablePairUtils.crossProductStream(List.of(), List.of(1)).count())
                .isZero();
    }

    @Test
    void zipStreamReturnsPairsInOrder() {
        List<Integer> left = List.of(1, 2, 3);
        List<String> right = List.of("a", "b", "c");

        List<ImmutablePair<Integer, String>> result =
                ImmutablePairUtils.zipStream(left, right).toList();

        assertThat(result)
                .containsExactly(new ImmutablePair<>(1, "a"), new ImmutablePair<>(2, "b"), new ImmutablePair<>(3, "c"));
    }

    @Test
    void zipStreamHandlesNullOrEmpty() {
        assertThat(ImmutablePairUtils.zipStream(null, List.of("a")).count()).isZero();
        assertThat(ImmutablePairUtils.zipStream(List.of(1), null).count()).isZero();
        assertThat(ImmutablePairUtils.zipStream(List.of(), List.of(1)).count()).isZero();
    }

    @Test
    void zipStreamThrowsForDifferentSizes() {
        List<Integer> left = List.of(1);
        List<String> right = List.of("a", "b");

        assertThatThrownBy(() -> ImmutablePairUtils.zipStream(left, right))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("same size");
    }
}
