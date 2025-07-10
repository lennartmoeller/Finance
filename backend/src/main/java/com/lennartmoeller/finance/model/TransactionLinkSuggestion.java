package com.lennartmoeller.finance.model;

import com.lennartmoeller.finance.util.DateRange;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@RequiredArgsConstructor
@Table(
        name = "transaction_link_suggestions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"bank_transaction", "transaction"}))
public class TransactionLinkSuggestion extends BaseModel {
    private static final int PROBABILITY_MATCH_WINDOW_DAYS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_transaction", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BankTransaction bankTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Transaction transaction;

    @Column(nullable = false)
    private Double probability;

    @Enumerated(EnumType.STRING)
    @Column(name = "link_state", nullable = false)
    private TransactionLinkState linkState = TransactionLinkState.UNDECIDED;

    public static TransactionLinkSuggestion of(BankTransaction bankTransaction, Transaction transaction) {
        TransactionLinkSuggestion suggestion = new TransactionLinkSuggestion();
        suggestion.setBankTransaction(bankTransaction);
        suggestion.setTransaction(transaction);
        suggestion.setProbability(suggestion.calculateProbability());
        suggestion.setLinkState(suggestion.getDefaultLinkState());
        return suggestion;
    }

    public boolean isConfirmed() {
        return linkState == TransactionLinkState.AUTO_CONFIRMED || linkState == TransactionLinkState.CONFIRMED;
    }

    public boolean hasNoManualLinkStateDecision() {
        return linkState == TransactionLinkState.UNDECIDED || linkState == TransactionLinkState.AUTO_CONFIRMED || linkState == TransactionLinkState.AUTO_REJECTED;
    }

    public TransactionLinkState getDefaultLinkState() {
        return probability == 1.0 ? TransactionLinkState.AUTO_CONFIRMED : TransactionLinkState.UNDECIDED;
    }

    public double calculateProbability() {
        // check if accounts match
        if (!bankTransaction.getAccount().getId().equals(transaction.getAccount().getId())) {
            return 0.0;
        }
        // check if amounts match
        if (!bankTransaction.getAmount().equals(transaction.getAmount())) {
            return 0.0;
        }
        // check if dates are within the match window
        DateRange dateRange = new DateRange(bankTransaction.getBookingDate(), transaction.getDate());
        long daysBetween = Math.abs(dateRange.getDays() - 1);
        if (daysBetween > PROBABILITY_MATCH_WINDOW_DAYS) {
            return 0.0;
        }
        // calculate probability based on the days between
        return 1.0 - daysBetween / (2.0 * PROBABILITY_MATCH_WINDOW_DAYS);
    }

    public boolean isUseful() {
        return 0.0 < probability && probability <= 1.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionLinkSuggestion that)) {
            return false;
        }
        if (this.getId() != null && that.getId() != null && this.id.equals(that.getId())) {
            return true;
        }
        return this.bankTransaction.equals(that.bankTransaction) && this.transaction.equals(that.transaction);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
