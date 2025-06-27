package com.lennartmoeller.finance.model;

import jakarta.persistence.*;
import java.time.YearMonth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Table(name = "inflation_rates")
public class InflationRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`year_month`", nullable = false, unique = true)
    private YearMonth yearMonth;

    @Column(nullable = false)
    private Double rate;
}
