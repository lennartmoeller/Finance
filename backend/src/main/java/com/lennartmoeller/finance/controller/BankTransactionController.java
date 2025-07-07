package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.BankTransactionImportResultDTO;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.service.BankCsvImportService;
import com.lennartmoeller.finance.service.BankTransactionService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bank-transactions")
@RequiredArgsConstructor
public class BankTransactionController {
    private final BankCsvImportService importService;
    private final BankTransactionService service;

    @PostMapping("/import")
    public BankTransactionImportResultDTO importCsv(
            @RequestParam("type") BankType type, @RequestParam("file") MultipartFile file) throws IOException {
        return importService.importCsv(type, file);
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

    @PostMapping
    public BankTransactionDTO createOrUpdateBankTransaction(@RequestBody BankTransactionDTO bankTransactionDTO) {
        Optional<BankTransactionDTO> optional =
                Optional.ofNullable(bankTransactionDTO.getId()).flatMap(service::findById);
        if (optional.isEmpty()) {
            bankTransactionDTO.setId(null);
        }
        return service.save(bankTransactionDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankTransaction(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
