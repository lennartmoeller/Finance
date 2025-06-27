package com.lennartmoeller.finance.projection;

import com.lennartmoeller.finance.model.Category;
import java.time.LocalDate;

public interface DailyBalanceProjection {

    LocalDate getDate();

    Category getCategory();

    Long getBalance();
}
