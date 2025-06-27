package com.lennartmoeller.finance.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lennartmoeller.finance.model.InflationRate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.properties.hibernate.hbm2ddl.import_files="
        })
class InflationRateRepositoryTest {

    @Autowired
    private InflationRateRepository inflationRateRepository;

    @Test
    void testCrudOperations() {
        InflationRate rate = new InflationRate();
        rate.setYearMonth(YearMonth.of(2021, 1));
        rate.setRate(1.5);
        rate = inflationRateRepository.save(rate);

        Optional<InflationRate> found = inflationRateRepository.findById(rate.getId());
        assertTrue(found.isPresent());
        assertEquals(YearMonth.of(2021, 1), found.get().getYearMonth());

        List<InflationRate> all = inflationRateRepository.findAll();
        assertEquals(1, all.size());

        inflationRateRepository.delete(found.get());
        assertTrue(inflationRateRepository.findAll().isEmpty());
    }
}
