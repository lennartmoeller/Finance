package com.lennartmoeller.finance.model;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private Boolean linked = false;
}
