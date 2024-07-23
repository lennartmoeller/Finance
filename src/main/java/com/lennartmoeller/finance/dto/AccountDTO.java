package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AccountDTO {
	private Long id;
	private String label;
	private Long startBalance;
	private Boolean active;
}
