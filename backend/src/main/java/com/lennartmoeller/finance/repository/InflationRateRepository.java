package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.InflationRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InflationRateRepository extends JpaRepository<InflationRate, Long> {}
