package com.lennartmoeller.finance;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class FinanceApplicationTest {

	@Test
	void mainStartsApplication() {
		try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
			String[] args = {"--test"};
			FinanceApplication.main(args);
			mocked.verify(() -> SpringApplication.run(FinanceApplication.class, args));
		}
	}
}
