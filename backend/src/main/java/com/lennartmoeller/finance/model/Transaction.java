package com.lennartmoeller.finance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account", nullable = false)
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category", nullable = false)
	private Category category;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private LocalDate date = LocalDate.now();

	@Column(nullable = false)
	private Long amount;

	@Column(nullable = false)
	private String description = "";

	@Column(nullable = false)
	private Boolean pinned = false;

}
