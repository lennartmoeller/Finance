package com.lennartmoeller.finance.csv;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import java.io.InputStream;
import java.util.List;

public interface BankCsvParser<T extends BankTransactionDTO> {
    List<T> parse(InputStream inputStream);
}
