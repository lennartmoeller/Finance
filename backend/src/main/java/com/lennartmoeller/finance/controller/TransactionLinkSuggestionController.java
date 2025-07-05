package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.TransactionLinkSuggestionDTO;
import com.lennartmoeller.finance.service.TransactionLinkSuggestionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction-link-suggestions")
@RequiredArgsConstructor
public class TransactionLinkSuggestionController {
    private final TransactionLinkSuggestionService service;

    @GetMapping
    public List<TransactionLinkSuggestionDTO> getTransactionLinkSuggestions() {
        return service.findAll();
    }

    @PostMapping("/generate")
    public List<TransactionLinkSuggestionDTO> generateTransactionLinkSuggestions() {
        return service.generateSuggestions(null, null);
    }
}
