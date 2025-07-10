package com.lennartmoeller.finance.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ModelCommonBehaviorTest {

    @Nested
    @DisplayName("Default values")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DefaultValues {
        Stream<Arguments> defaults() {
            return Stream.of(
                    Arguments.of("Account active", (Supplier<Object>) () -> new Account().getActive(), true),
                    Arguments.of("Account deposits", (Supplier<Object>) () -> new Account().getDeposits(), false),
                    Arguments.of("Account iban", (Supplier<Object>) () -> new Account().getIban(), null),
                    Arguments.of(
                            "Category smooth type",
                            (Supplier<Object>) () -> new Category().getSmoothType(),
                            CategorySmoothType.DAILY),
                    Arguments.of(
                            "Transaction date", (Supplier<Object>) () -> new Transaction().getDate(), LocalDate.now()),
                    Arguments.of(
                            "Transaction description", (Supplier<Object>) () -> new Transaction().getDescription(), ""),
                    Arguments.of("Transaction pinned", (Supplier<Object>) () -> new Transaction().getPinned(), false),
                    Arguments.of(
                            "Link suggestion state",
                            (Supplier<Object>) () -> new TransactionLinkSuggestion().getLinkState(),
                            TransactionLinkState.UNDECIDED));
        }

        @ParameterizedTest(name = "{0} defaults to {2}")
        @MethodSource("defaults")
        void verifyDefaultValues(Supplier<Object> supplier, Object expected) {
            assertThat(supplier.get()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Equality and hash code")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Equality {
        Stream<Arguments> entities() {
            return Stream.of(
                    Arguments.of(
                            "Account", (Supplier<Object>) Account::new, (BiConsumer<Account, Long>) Account::setId),
                    Arguments.of(
                            "Category", (Supplier<Object>) Category::new, (BiConsumer<Category, Long>) Category::setId),
                    Arguments.of(
                            "InflationRate", (Supplier<Object>) InflationRate::new, (BiConsumer<InflationRate, Long>)
                                    InflationRate::setId),
                    Arguments.of("Target", (Supplier<Object>) Target::new, (BiConsumer<Target, Long>) Target::setId),
                    Arguments.of("Transaction", (Supplier<Object>) Transaction::new, (BiConsumer<Transaction, Long>)
                            Transaction::setId),
                    Arguments.of(
                            "LinkSuggestion",
                            (Supplier<Object>) () -> {
                                TransactionLinkSuggestion s = new TransactionLinkSuggestion();
                                s.setBankTransaction(new BankTransaction());
                                s.setTransaction(new Transaction());
                                return s;
                            },
                            (BiConsumer<TransactionLinkSuggestion, Long>) TransactionLinkSuggestion::setId),
                    Arguments.of("BankTransaction", (Supplier<Object>) BankTransaction::new, (BiConsumer<
                            BankTransaction, Long>)
                            BankTransaction::setId));
        }

        @ParameterizedTest(name = "{0} equality based on id")
        @MethodSource("entities")
        void verifyEquality(Supplier<Object> factory, BiConsumer<Object, Long> idSetter) {
            Object first = factory.get();
            Object second = factory.get();
            idSetter.accept(first, 1L);
            idSetter.accept(second, 1L);

            assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);

            idSetter.accept(second, 2L);
            assertThat(first).isNotEqualTo(second);
        }
    }
}
