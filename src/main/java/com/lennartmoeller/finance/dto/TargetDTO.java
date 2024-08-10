package com.lennartmoeller.finance.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Setter
public class TargetDTO {
	private Long id;
	private Long categoryId;
	private LocalDate start;
	private LocalDate end;
	private Long amount;
}
