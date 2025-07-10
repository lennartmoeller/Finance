package com.lennartmoeller.finance.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TransactionLinkSuggestionTest {

    private static Account account(long id) {
        Account a = new Account();
        a.setId(id);
        return a;
    }

    private static BankTransaction bankTx(long accId, long amount, LocalDate date) {
        BankTransaction b = new BankTransaction();
        b.setAccount(account(accId));
        b.setAmount(amount);
        b.setBookingDate(date);
        return b;
    }

    private static Transaction tx(long accId, long amount, LocalDate date) {
        Transaction t = new Transaction();
        t.setAccount(account(accId));
        t.setAmount(amount);
        t.setDate(date);
        return t;
    }

    @Nested
    class ProbabilityCalculation {
        @Test
        void returnsOneForExactMatch() {
            BankTransaction b = bankTx(1L, 10L, LocalDate.of(2024, 1, 1));
            Transaction t = tx(1L, 10L, LocalDate.of(2024, 1, 1));
            TransactionLinkSuggestion s = TransactionLinkSuggestion.of(b, t);

            assertThat(s.getProbability()).isEqualTo(1.0);
            assertThat(s.getLinkState()).isEqualTo(TransactionLinkState.AUTO_CONFIRMED);
        }

        @Test
        void returnsZeroOnMismatchedAccountOrAmount() {
            BankTransaction b = bankTx(1L, 5L, LocalDate.of(2024, 1, 2));
            Transaction t = tx(2L, 6L, LocalDate.of(2024, 1, 2));
            TransactionLinkSuggestion s = TransactionLinkSuggestion.of(b, t);

            assertThat(s.getProbability()).isZero();
        }

        @Test
        void returnsZeroWhenDatesOutsideWindow() {
            BankTransaction b = bankTx(1L, 5L, LocalDate.of(2024, 1, 1));
            Transaction t = tx(1L, 5L, LocalDate.of(2024, 1, 20));
            TransactionLinkSuggestion s = TransactionLinkSuggestion.of(b, t);

            assertThat(s.getProbability()).isZero();
        }

        @Test
        void calculatesIntermediateProbability() {
            BankTransaction b = bankTx(1L, 5L, LocalDate.of(2024, 1, 1));
            Transaction t = tx(1L, 5L, LocalDate.of(2024, 1, 3));
            TransactionLinkSuggestion s = TransactionLinkSuggestion.of(b, t);

            assertThat(s.getProbability()).isCloseTo(12.0 / 14.0, within(1e-6));
            assertThat(s.getLinkState()).isEqualTo(TransactionLinkState.UNDECIDED);
        }
    }

    @Nested
    class Helpers {
        @ParameterizedTest
        @EnumSource(TransactionLinkState.class)
        void confirmCheck(TransactionLinkState state) {
            TransactionLinkSuggestion s = new TransactionLinkSuggestion();
            s.setLinkState(state);

            boolean expected = state == TransactionLinkState.AUTO_CONFIRMED || state == TransactionLinkState.CONFIRMED;
            assertThat(s.isConfirmed()).isEqualTo(expected);
        }

        @ParameterizedTest
        @EnumSource(TransactionLinkState.class)
        void noManualDecision(TransactionLinkState state) {
            TransactionLinkSuggestion s = new TransactionLinkSuggestion();
            s.setLinkState(state);

            boolean expected = state == TransactionLinkState.UNDECIDED
                    || state == TransactionLinkState.AUTO_CONFIRMED
                    || state == TransactionLinkState.AUTO_REJECTED;
            assertThat(s.hasNoManualLinkStateDecision()).isEqualTo(expected);
        }

        @Test
        void usefulOnlyWhenProbabilityPositive() {
            TransactionLinkSuggestion s = new TransactionLinkSuggestion();
            s.setProbability(0.0);
            assertThat(s.isUseful()).isFalse();
            s.setProbability(1.2);
            assertThat(s.isUseful()).isFalse();
            s.setProbability(0.5);
            assertThat(s.isUseful()).isTrue();
        }
    }

    @Test
    @DisplayName("Equality uses id if present, otherwise references")
    void equalsUsesIdAndReferences() {
        BankTransaction b1 = bankTx(1L, 5L, LocalDate.now());
        b1.setId(1L);
        Transaction t1 = tx(1L, 5L, LocalDate.now());
        t1.setId(1L);
        TransactionLinkSuggestion first = TransactionLinkSuggestion.of(b1, t1);
        first.setId(1L);

        BankTransaction b2 = bankTx(2L, 5L, LocalDate.now());
        b2.setId(2L);
        Transaction t2 = tx(2L, 5L, LocalDate.now());
        t2.setId(2L);
        TransactionLinkSuggestion second = TransactionLinkSuggestion.of(b2, t2);
        second.setId(1L);

        assertThat(first).isEqualTo(second);

        second.setId(2L);
        assertThat(first).isNotEqualTo(second);

        TransactionLinkSuggestion third = TransactionLinkSuggestion.of(b1, t1);
        assertThat(first).isEqualTo(third);

        third.setBankTransaction(b2);
        third.setTransaction(t2);
        assertThat(first).isNotEqualTo(third);
    }
}
