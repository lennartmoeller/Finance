package com.lennartmoeller.finance.service;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.mapper.BankTransactionMapper;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.repository.AccountRepository;
import com.lennartmoeller.finance.repository.BankTransactionRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankTransactionService {
    private final BankTransactionRepository repository;
    private final BankTransactionMapper mapper;
    private final AccountRepository accountRepository;
    private final TransactionLinkSuggestionService suggestionService;

    public List<BankTransactionDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public Optional<BankTransactionDTO> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    public BankTransactionDTO save(BankTransactionDTO dto) {
        Account account = null;
        if (dto.getIban() != null) {
            account = accountRepository.findAllByIbanIn(Collections.singleton(dto.getIban())).stream()
                    .findFirst()
                    .orElse(null);
        }
        BankTransaction entity = mapper.toEntity(dto, account);
        BankTransaction saved = repository.save(entity);
        suggestionService.updateForBankTransactions(List.of(saved));
        return mapper.toDto(saved);
    }

    public void deleteById(Long id) {
        suggestionService.removeForBankTransaction(id);
        repository.deleteById(id);
    }
}
