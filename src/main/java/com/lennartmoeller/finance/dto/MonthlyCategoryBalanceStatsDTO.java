package com.lennartmoeller.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lennartmoeller.finance.model.TransactionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Setter
public class MonthlyCategoryBalanceStatsDTO {

	private List<TransactionTypeCategoryStatsDTO> stats;
	private @Nullable LocalDate startDate;
	private @Nullable LocalDate endDate;

	public static MonthlyCategoryBalanceStatsDTO empty() {
		MonthlyCategoryBalanceStatsDTO monthlyCategoryBalanceStatsDTO = new MonthlyCategoryBalanceStatsDTO();
		monthlyCategoryBalanceStatsDTO.stats = Arrays.stream(TransactionType.values())
			.map(transactionType -> TransactionTypeCategoryStatsDTO.empty(transactionType, null))
			.toList();
		return monthlyCategoryBalanceStatsDTO;
	}

	@JsonProperty
	public RowStatsDTO getTotalStats() {
		Map<YearMonth, CellStatsDTO> totalStats = this.stats.stream()
			.flatMap(transactionTypeCategoryStatsDTO -> transactionTypeCategoryStatsDTO.getCategoryStats().stream())
			.flatMap(categoryStatsNodeDTO -> categoryStatsNodeDTO.getStats().getMonthly().entrySet().stream())
			.collect(Collectors.groupingBy(
				Map.Entry::getKey,
				Collectors.mapping(Map.Entry::getValue, Collectors.toList())
			))
			.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> CellStatsDTO.add(entry.getValue())
			));

		return new RowStatsDTO(totalStats);
	}

}
