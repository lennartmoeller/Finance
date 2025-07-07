package com.lennartmoeller.finance.csv;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import java.io.InputStream;
import java.util.List;
import javax.annotation.Nonnull;

public interface BankCsvParser<T extends BankTransactionDTO> {
    @Nonnull
    List<T> parse(@Nonnull InputStream inputStream);
}
