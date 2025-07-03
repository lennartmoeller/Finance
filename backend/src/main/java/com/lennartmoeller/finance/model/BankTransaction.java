package com.lennartmoeller.finance.model;

import com.lennartmoeller.finance.converter.MapToJsonStringConverter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@RequiredArgsConstructor
@Table(
        name = "bank_transactions",
        uniqueConstraints =
                @UniqueConstraint(columnNames = {"account", "booking_date", "purpose", "counterparty", "amount"}))
public class BankTransaction extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankType bank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account", nullable = false)
    private Account account;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(nullable = false, length = 1024)
    private String purpose;

    @Column(nullable = false, length = 1024)
    private String counterparty;

    @Column(nullable = false)
    private Long amount;

    @Lob
    @Convert(converter = MapToJsonStringConverter.class)
    private Map<String, String> data = new HashMap<>();
}
