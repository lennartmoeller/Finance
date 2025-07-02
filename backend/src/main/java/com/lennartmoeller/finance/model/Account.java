package com.lennartmoeller.finance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@RequiredArgsConstructor
@Table(name = "accounts")
public class Account extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String label;

    @Column(nullable = false)
    private Long startBalance;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean deposits = false;
}
