package com.lennartmoeller.finance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
	private CategoryType type;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date start;

	@Column
	@Temporal(TemporalType.DATE)
	private Date end;

	@Column
	private Long monthlyBudget;

}
