package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.CategoryDTO;
import com.lennartmoeller.finance.dto.CategoryStatsDTO;
import com.lennartmoeller.finance.dto.MonthlyCategoryStatsDTO;
import com.lennartmoeller.finance.dto.RowStatsDTO;
import com.lennartmoeller.finance.mapper.CategoryMapper;
import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.model.TransactionType;
import com.lennartmoeller.finance.projection.DailyBalanceProjection;
import com.lennartmoeller.finance.repository.CategoryRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MonthlyCategoryBalanceStatsServiceTest {
    private CategoryMapper categoryMapper;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private MonthlyCategoryBalanceStatsService service;

    @BeforeEach
    void setUp() {
        categoryMapper = mock(CategoryMapper.class);
        categoryRepository = mock(CategoryRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        service = new MonthlyCategoryBalanceStatsService(categoryMapper, categoryRepository, transactionRepository);
    }

    @Test
    void testGetStatsEmpty() {
        when(transactionRepository.getDailyBalances()).thenReturn(List.of());

        MonthlyCategoryStatsDTO dto = service.getStats();

        assertNotNull(dto.getStats());
        assertTrue(dto.getStats().values().stream()
                .allMatch(s -> s.getCategoryStats().isEmpty()));
    }

    @Test
    void testGetStatsWithData() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        Category expParent = new Category();
        expParent.setId(1L);
        expParent.setTransactionType(TransactionType.EXPENSE);
        expParent.setSmoothType(CategorySmoothType.DAILY);
        Category expChild = new Category();
        expChild.setId(2L);
        expChild.setParent(expParent);
        expChild.setTransactionType(TransactionType.EXPENSE);
        expChild.setSmoothType(CategorySmoothType.DAILY);
        expParent.setTargets(List.of());
        expChild.setTargets(List.of());
        Category incomeCat = new Category();
        incomeCat.setId(3L);
        incomeCat.setTransactionType(TransactionType.INCOME);
        incomeCat.setSmoothType(CategorySmoothType.DAILY);
        incomeCat.setTargets(List.of());

        when(categoryRepository.findAll()).thenReturn(List.of(expParent, expChild, incomeCat));

        CategoryDTO expParentDto = new CategoryDTO();
        CategoryDTO expChildDto = new CategoryDTO();
        CategoryDTO incomeDto = new CategoryDTO();
        when(categoryMapper.toDto(expParent)).thenReturn(expParentDto);
        when(categoryMapper.toDto(expChild)).thenReturn(expChildDto);
        when(categoryMapper.toDto(incomeCat)).thenReturn(incomeDto);

        List<DailyBalanceProjection> projections = List.of(
                new SimpleProjection(start, expChild, -10L),
                new SimpleProjection(start, incomeCat, 20L),
                new SimpleProjection(start.plusDays(1), incomeCat, 30L));
        when(transactionRepository.getDailyBalances()).thenReturn(projections);

        MonthlyCategoryStatsDTO result = service.getStats();

        YearMonth month = YearMonth.from(start);
        // income surplus should sum to 50
        RowStatsDTO incomeTotal = result.getStats().get(TransactionType.INCOME).getTotalStats();
        assertEquals(50.0, incomeTotal.getMonthly().get(month).getSurplus().getRaw());
        // expense parent should aggregate child (-10)
        RowStatsDTO expenseTotal =
                result.getStats().get(TransactionType.EXPENSE).getTotalStats();
        assertEquals(-10.0, expenseTotal.getMonthly().get(month).getSurplus().getRaw());
        // investment stats should be empty but still have month entry
        RowStatsDTO investTotal =
                result.getStats().get(TransactionType.INVESTMENT).getTotalStats();
        assertEquals(0.0, investTotal.getMonthly().get(month).getSurplus().getRaw());

        assertEquals(start, result.getStartDate());
        assertEquals(LocalDate.now(), result.getEndDate());
    }

    @Test
    void testGetStatsWithTargets() {
        LocalDate start = LocalDate.of(2021, 1, 1);
        Category cat = new Category();
        cat.setId(1L);
        cat.setTransactionType(TransactionType.INCOME);
        cat.setSmoothType(CategorySmoothType.DAILY);

        Target target = new Target();
        target.setCategory(cat);
        target.setStartDate(start);
        target.setEndDate(start.plusDays(1));
        target.setAmount(100L);
        cat.setTargets(List.of(target));

        when(categoryRepository.findAll()).thenReturn(List.of(cat));
        when(categoryMapper.toDto(cat)).thenReturn(new CategoryDTO());

        List<DailyBalanceProjection> balances =
                List.of(new SimpleProjection(start, cat, 50L), new SimpleProjection(start.plusDays(1), cat, 50L));
        when(transactionRepository.getDailyBalances()).thenReturn(balances);

        MonthlyCategoryStatsDTO result = service.getStats();
        YearMonth ym = YearMonth.of(2021, 1);
        double targetValue = result.getStats()
                .get(TransactionType.INCOME)
                .getTotalStats()
                .getMonthly()
                .get(ym)
                .getTarget();
        assertEquals(100.0 / 31 * 2, targetValue, 1e-9);
    }

    @Test
    void testChildCategoriesSortedByAbsoluteSurplus() {
        LocalDate date = LocalDate.of(2021, 2, 1);

        Category parent = new Category();
        parent.setId(1L);
        parent.setTransactionType(TransactionType.EXPENSE);
        parent.setSmoothType(CategorySmoothType.DAILY);
        parent.setTargets(List.of());

        Category childA = new Category();
        childA.setId(2L);
        childA.setParent(parent);
        childA.setTransactionType(TransactionType.EXPENSE);
        childA.setSmoothType(CategorySmoothType.DAILY);
        childA.setTargets(List.of());

        Category childB = new Category();
        childB.setId(3L);
        childB.setParent(parent);
        childB.setTransactionType(TransactionType.EXPENSE);
        childB.setSmoothType(CategorySmoothType.DAILY);
        childB.setTargets(List.of());

        when(categoryRepository.findAll()).thenReturn(List.of(parent, childA, childB));

        CategoryDTO parentDto = new CategoryDTO();
        CategoryDTO childADto = new CategoryDTO();
        CategoryDTO childBDto = new CategoryDTO();
        when(categoryMapper.toDto(parent)).thenReturn(parentDto);
        when(categoryMapper.toDto(childA)).thenReturn(childADto);
        when(categoryMapper.toDto(childB)).thenReturn(childBDto);

        List<DailyBalanceProjection> projections =
                List.of(new SimpleProjection(date, childA, -10L), new SimpleProjection(date, childB, -20L));
        when(transactionRepository.getDailyBalances()).thenReturn(projections);

        MonthlyCategoryStatsDTO result = service.getStats();

        List<CategoryStatsDTO> children = result.getStats()
                .get(TransactionType.EXPENSE)
                .getCategoryStats()
                .getFirst()
                .getChildren();

        assertEquals(childBDto, children.get(0).getCategory());
        assertEquals(childADto, children.get(1).getCategory());
    }

    private static class SimpleProjection implements DailyBalanceProjection {
        private final LocalDate date;
        private final Category category;
        private final Long balance;

        SimpleProjection(LocalDate date, Category category, Long balance) {
            this.date = date;
            this.category = category;
            this.balance = balance;
        }

        @Override
        public LocalDate getDate() {
            return date;
        }

        @Override
        public Category getCategory() {
            return category;
        }

        @Override
        public Long getBalance() {
            return balance;
        }
    }
}
