package com.lennartmoeller.finance.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Table(name = "targets")
public class Target {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category", nullable = false)
    private Category category;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Column
    @Temporal(TemporalType.DATE)
    private LocalDate endDate;

    @Column
    private Long amount;
}
