package com.lennartmoeller.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionDTO {
	private Long id;
	private Long account;
	private Long category;
	private Date date;
	private Long amount;
	private String description;
}
