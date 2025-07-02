package com.lennartmoeller.finance.model;

import jakarta.persistence.*;
import java.time.YearMonth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@EqualsAndHashCode(of = "id")
@RequiredArgsConstructor
@Table(name = "inflation_rates")
public class InflationRate extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`year_month`", nullable = false, unique = true)
    private YearMonth yearMonth;

    @Column(nullable = false)
    private Double rate;
}
