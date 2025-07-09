package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.mapper.BankTransactionMapper;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankTransactionService {
    private final BankTransactionMapper mapper;
    private final BankTransactionRepository repository;

    public List<BankTransactionDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public Optional<BankTransactionDTO> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }
}
