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
@EqualsAndHashCode(of = "id")
@RequiredArgsConstructor
@Table(
        name = "bank_transactions",
        uniqueConstraints =
                @UniqueConstraint(columnNames = {"iban", "booking_date", "purpose", "counterparty", "amount"}))
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankType bank;

    @Column(nullable = false)
    private String iban;

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
