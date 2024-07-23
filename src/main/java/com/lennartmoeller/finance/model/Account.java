package com.lennartmoeller.finance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String label;

	@Column(nullable = false)
	private Long startBalance;

	@Column(nullable = false)
	private Boolean active = true;

}
