package com.lennartmoeller.finance.repository;

import com.lennartmoeller.finance.model.Category;
import com.lennartmoeller.finance.model.Target;
import com.lennartmoeller.finance.model.TransactionType;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.jpa.properties.hibernate.hbm2ddl.import_files="
})
class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private TestEntityManager entityManager;

	@Test
	void testFindByIdFetchTargets() {
		Category c = new Category();
		c.setLabel("C1");
		c.setTransactionType(TransactionType.EXPENSE);
		categoryRepository.save(c);

		Target t1 = new Target();
		t1.setCategory(c);
		t1.setStartDate(LocalDate.now());
		entityManager.persist(t1);
		Target t2 = new Target();
		t2.setCategory(c);
		t2.setStartDate(LocalDate.now());
		entityManager.persist(t2);
		entityManager.flush();
		entityManager.clear();

		Optional<Category> opt = categoryRepository.findById(c.getId());
		assertTrue(opt.isPresent());
		Category loaded = opt.get();
		assertTrue(Hibernate.isInitialized(loaded.getTargets()));
		assertEquals(2, loaded.getTargets().size());
	}

	@Test
	void testFindAllFetchTargets() {
		Category c1 = new Category();
		c1.setLabel("C1");
		c1.setTransactionType(TransactionType.INCOME);
		categoryRepository.save(c1);

		Target t = new Target();
		t.setCategory(c1);
		t.setStartDate(LocalDate.now());
		entityManager.persist(t);
		entityManager.flush();
		entityManager.clear();

		List<Category> all = categoryRepository.findAll();
		assertEquals(1, all.size());
		Category loaded = all.getFirst();
		assertTrue(Hibernate.isInitialized(loaded.getTargets()));
		assertEquals(1, loaded.getTargets().size());
	}
}
