package com.lennartmoeller.finance.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.DailySavingStatsDTO;
import com.lennartmoeller.finance.dto.MonthlyCategoryStatsDTO;
import com.lennartmoeller.finance.dto.MonthlySavingStatsDTO;
import com.lennartmoeller.finance.service.DailyBalanceStatsService;
import com.lennartmoeller.finance.service.MonthlyCategoryBalanceStatsService;
import com.lennartmoeller.finance.service.MonthlySavingStatsService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {
    @Mock
    private DailyBalanceStatsService dailyService;

    @Mock
    private MonthlyCategoryBalanceStatsService categoryService;

    @Mock
    private MonthlySavingStatsService savingService;

    @InjectMocks
    private StatisticsController controller;

    @Test
    void shouldReturnDailyBalances() {
        List<DailySavingStatsDTO> list = List.of(new DailySavingStatsDTO());
        when(dailyService.getStats()).thenReturn(list);

        List<DailySavingStatsDTO> result = controller.getDailyBalances();

        assertThat(result).isEqualTo(list);
        verify(dailyService).getStats();
    }

    @Test
    void shouldReturnMonthlySavings() {
        List<MonthlySavingStatsDTO> list = List.of(new MonthlySavingStatsDTO());
        when(savingService.getStats()).thenReturn(list);

        List<MonthlySavingStatsDTO> result = controller.getMonthlySavings();

        assertThat(result).isEqualTo(list);
        verify(savingService).getStats();
    }

    @Test
    void shouldReturnMonthlyCategoryBalances() {
        MonthlyCategoryStatsDTO dto = new MonthlyCategoryStatsDTO();
        when(categoryService.getStats()).thenReturn(dto);

        MonthlyCategoryStatsDTO result = controller.getMonthlyCategoryBalances();

        assertThat(result).isEqualTo(dto);
        verify(categoryService).getStats();
    }
}
