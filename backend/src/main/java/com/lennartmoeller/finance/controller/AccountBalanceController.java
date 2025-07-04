package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.AccountBalanceDTO;
import com.lennartmoeller.finance.service.AccountBalanceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accountBalances")
@RequiredArgsConstructor
public class AccountBalanceController {
    private final AccountBalanceService accountBalanceService;

    @GetMapping
    public List<AccountBalanceDTO> getAccountBalances() {
        return accountBalanceService.findAll();
    }
}
