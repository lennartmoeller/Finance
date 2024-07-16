package com.lennartmoeller.finance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
	private Date date = new Date();

	@Column(nullable = false)
	private Long amount;

	@Column(nullable = false)
	private String description = "";

}
