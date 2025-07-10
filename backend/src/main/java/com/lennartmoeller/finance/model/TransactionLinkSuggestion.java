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
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@RequiredArgsConstructor
@Table(
        name = "transaction_link_suggestions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"bank_transaction", "transaction"}))
public class TransactionLinkSuggestion extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_transaction", nullable = false)
    private BankTransaction bankTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction", nullable = false)
    private Transaction transaction;

    @Column(nullable = false)
    private Double probability;

    @Enumerated(EnumType.STRING)
    @Column(name = "link_state", nullable = false)
    private TransactionLinkState linkState = TransactionLinkState.UNDECIDED;

    public boolean isConfirmed() {
        return linkState == TransactionLinkState.AUTO_CONFIRMED || linkState == TransactionLinkState.CONFIRMED;
    }

    public boolean isRejected() {
        return linkState == TransactionLinkState.AUTO_REJECTED || linkState == TransactionLinkState.REJECTED;
    }

    public TransactionLinkState getDefaultLinkState() {
        return probability == 1.0 ? TransactionLinkState.AUTO_CONFIRMED : TransactionLinkState.UNDECIDED;
    }

    public static double calculateProbability(
            BankTransaction bankTransaction, Transaction transaction, int windowDays) {
        long daysBetween =
                Math.abs(new DateRange(bankTransaction.getBookingDate(), transaction.getDate()).getDays() - 1);
        return 1.0 - daysBetween / (2.0 * windowDays);
    }
}
