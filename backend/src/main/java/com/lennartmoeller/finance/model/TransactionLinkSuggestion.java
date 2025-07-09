package com.lennartmoeller.finance.model;

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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
}
