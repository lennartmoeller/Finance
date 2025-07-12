package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.mapper.BankTransactionMapper;
import com.lennartmoeller.finance.mapper.CamtV8TransactionMapper;
import com.lennartmoeller.finance.mapper.IngV1TransactionMapper;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankTransactionService {
    private final IngV1TransactionMapper ingMapper;
    private final CamtV8TransactionMapper camtMapper;
    private final BankTransactionRepository repository;

    public List<BankTransactionDTO> findAll() {
        return repository.findAll().stream()
                .map(t -> mapperFor(t.getBank()).toDto(t))
                .toList();
    }

    public Optional<BankTransactionDTO> findById(Long id) {
        return repository.findById(id).map(t -> mapperFor(t.getBank()).toDto(t));
    }

    private BankTransactionMapper mapperFor(com.lennartmoeller.finance.model.BankType bank) {
        return bank == com.lennartmoeller.finance.model.BankType.ING_V1 ? ingMapper : camtMapper;
    }
}
