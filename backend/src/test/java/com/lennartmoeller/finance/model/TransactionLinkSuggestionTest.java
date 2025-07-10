package com.lennartmoeller.finance.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TransactionLinkSuggestionTest {

    @Test
    void testIsConfirmed() {
        TransactionLinkSuggestion s = new TransactionLinkSuggestion();
        s.setLinkState(TransactionLinkState.AUTO_CONFIRMED);
        assertThat(s.isConfirmed()).isTrue();
        s.setLinkState(TransactionLinkState.CONFIRMED);
        assertThat(s.isConfirmed()).isTrue();
        s.setLinkState(TransactionLinkState.UNDECIDED);
        assertThat(s.isConfirmed()).isFalse();
    }

    @Test
    void testIsRejected() {
        TransactionLinkSuggestion s = new TransactionLinkSuggestion();
        s.setLinkState(TransactionLinkState.REJECTED);
        assertThat(s.isRejected()).isTrue();
        s.setLinkState(TransactionLinkState.AUTO_REJECTED);
        assertThat(s.isRejected()).isTrue();
        s.setLinkState(TransactionLinkState.UNDECIDED);
        assertThat(s.isRejected()).isFalse();
    }

    @Test
    void testGetDefaultLinkState() {
        TransactionLinkSuggestion s = new TransactionLinkSuggestion();
        s.setProbability(1.0);
        assertThat(s.getDefaultLinkState()).isEqualTo(TransactionLinkState.AUTO_CONFIRMED);
        s.setProbability(0.5);
        assertThat(s.getDefaultLinkState()).isEqualTo(TransactionLinkState.UNDECIDED);
    }

    @Test
    void testCalculateProbability() {
        BankTransaction bank = new BankTransaction();
        bank.setBookingDate(java.time.LocalDate.of(2024, 1, 1));
        Transaction tx = new Transaction();
        tx.setDate(java.time.LocalDate.of(2024, 1, 3));

        double result = TransactionLinkSuggestion.calculateProbability(bank, tx, 7);

        assertThat(result).isCloseTo(0.857142, org.assertj.core.data.Offset.offset(1e-6));
    }
}
