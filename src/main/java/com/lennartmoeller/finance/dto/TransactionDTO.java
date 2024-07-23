package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class TransactionDTO {
	private Long id;
	private Long accountId;
	private Long categoryId;
	private LocalDate date;
	private Long amount;
	private String description;
}
