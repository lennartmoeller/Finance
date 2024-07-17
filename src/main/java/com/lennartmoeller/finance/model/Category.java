package com.lennartmoeller.finance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private Category parent;

	@Column(nullable = false)
	private String label;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	private TransactionType transactionType;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	private CategorySmoothType smoothType = CategorySmoothType.DAILY;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private LocalDate start;

	@Column
	@Temporal(TemporalType.DATE)
	private LocalDate end;

	@Column
	private Long target;

}
