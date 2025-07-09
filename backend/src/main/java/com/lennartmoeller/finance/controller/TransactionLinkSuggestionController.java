package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.Transaction;
import com.lennartmoeller.finance.model.TransactionLinkState;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import com.lennartmoeller.finance.repository.TransactionRepository;
import com.lennartmoeller.finance.service.TransactionLinkSuggestionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction-link-suggestions")
@RequiredArgsConstructor
public class TransactionLinkSuggestionController {
    private final TransactionLinkSuggestionService service;
    private final TransactionRepository transactionRepository;
    private final BankTransactionRepository bankTransactionRepository;

    @GetMapping
    public List<TransactionLinkSuggestionDTO> getTransactionLinkSuggestions() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionLinkSuggestionDTO> getTransactionLinkSuggestionById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound()
                .build());
    }

    @PostMapping("/{id}/link-state")
    public ResponseEntity<TransactionLinkSuggestionDTO> updateLinkState(
            @PathVariable Long id, @RequestParam TransactionLinkState linkState) {
        return service.updateLinkState(id, linkState).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound()
                .build());
    }

    @PostMapping("/generate")
    public List<TransactionLinkSuggestionDTO> generateTransactionLinkSuggestions(
            @RequestParam(required = false) List<Long> transactionIds,
            @RequestParam(required = false) List<Long> bankTransactionIds) {
        List<Transaction> transactions =
                transactionIds != null ? transactionRepository.findAllById(transactionIds) : null;
        List<BankTransaction> bankTransactions =
                bankTransactionIds != null ? bankTransactionRepository.findAllById(bankTransactionIds) : null;
        return service.generateSuggestions(transactions, bankTransactions);
    }
}
