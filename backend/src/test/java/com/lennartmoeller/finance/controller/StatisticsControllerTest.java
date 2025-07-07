package com.lennartmoeller.finance.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.DailySavingStatsDTO;
import com.lennartmoeller.finance.dto.MonthlyCategoryStatsDTO;
import com.lennartmoeller.finance.dto.MonthlySavingStatsDTO;
import com.lennartmoeller.finance.service.DailyBalanceStatsService;
import com.lennartmoeller.finance.service.MonthlyCategoryBalanceStatsService;
import com.lennartmoeller.finance.service.MonthlySavingStatsService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatisticsControllerTest {
    private DailyBalanceStatsService dailyService;
    private MonthlyCategoryBalanceStatsService categoryService;
    private MonthlySavingStatsService savingService;
    private StatisticsController controller;

    @BeforeEach
    void setUp() {
        dailyService = mock(DailyBalanceStatsService.class);
        categoryService = mock(MonthlyCategoryBalanceStatsService.class);
        savingService = mock(MonthlySavingStatsService.class);
        controller = new StatisticsController(dailyService, categoryService, savingService);
    }

    @Test
    void testGetDailyBalances() {
        List<DailySavingStatsDTO> list = List.of(new DailySavingStatsDTO());
        when(dailyService.getStats()).thenReturn(list);

        List<DailySavingStatsDTO> result = controller.getDailyBalances();

        assertEquals(list, result);
        verify(dailyService).getStats();
    }

    @Test
    void testGetMonthlySavings() {
        List<MonthlySavingStatsDTO> list = List.of(new MonthlySavingStatsDTO());
        when(savingService.getStats()).thenReturn(list);

        List<MonthlySavingStatsDTO> result = controller.getMonthlySavings();

        assertEquals(list, result);
        verify(savingService).getStats();
    }

    @Test
    void testGetMonthlyCategoryBalances() {
        MonthlyCategoryStatsDTO dto = new MonthlyCategoryStatsDTO();
        when(categoryService.getStats()).thenReturn(dto);

        MonthlyCategoryStatsDTO result = controller.getMonthlyCategoryBalances();

        assertEquals(dto, result);
        verify(categoryService).getStats();
    }
}
