package com.lennartmoeller.finance.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@RequiredArgsConstructor
@Table(name = "categories")
public class Category extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private Category parent;

    @Column(nullable = false, unique = true)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorySmoothType smoothType = CategorySmoothType.DAILY;

    @Column
    private String icon;

    // TODO: Maybe add domain name to fetch logo from https://docs.logo.dev

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Target> targets;
}
