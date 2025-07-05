package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.TransactionLinkSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLinkSuggestionRepository extends JpaRepository<TransactionLinkSuggestion, Long> {}
