package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.BankCsvImportStatsDTO;
import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.service.BankCsvImportService;
import com.lennartmoeller.finance.service.BankTransactionService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final BankTransactionService service;

    @PostMapping("/import")
    public BankCsvImportStatsDTO importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        return importService.importCsv(file);
    }

    @GetMapping
    public List<BankTransactionDTO> getBankTransactions() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankTransactionDTO> getBankTransactionById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound()
                .build());
    }
}
