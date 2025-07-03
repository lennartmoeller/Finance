package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.BankTransactionImportResultDTO;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.service.BankCsvImportService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bank-transactions")
@RequiredArgsConstructor
public class BankTransactionController {

    private final BankCsvImportService importService;

    @PostMapping("/import")
    public BankTransactionImportResultDTO importCsv(
            @RequestParam("type") BankType type, @RequestParam("file") MultipartFile file) throws IOException {
        return importService.importCsv(type, file);
    }
}
