package com.lennartmoeller.finance.controller;

import com.lennartmoeller.finance.dto.TransactionDTO;
import com.lennartmoeller.finance.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

	private TransactionService service;
	private TransactionController controller;

	@BeforeEach
	void setUp() {
		service = mock(TransactionService.class);
		controller = new TransactionController(service);
	}

	@Test
	void testGetTransactions() {
		List<Long> aIds = List.of(1L, 2L);
		List<Long> cIds = List.of(3L);
		List<YearMonth> months = List.of(YearMonth.of(2024, 1));
		List<TransactionDTO> list = List.of(new TransactionDTO());
		when(service.findFiltered(aIds, cIds, months)).thenReturn(list);

		List<TransactionDTO> result = controller.getTransactions(aIds, cIds, months);

		assertEquals(list, result);
		verify(service).findFiltered(aIds, cIds, months);
	}

	@Test
	void testGetTransactionByIdFound() {
		TransactionDTO dto = new TransactionDTO();
		when(service.findById(1L)).thenReturn(Optional.of(dto));

		ResponseEntity<TransactionDTO> response = controller.getTransactionById(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(dto, response.getBody());
	}

	@Test
	void testGetTransactionByIdNotFound() {
		when(service.findById(2L)).thenReturn(Optional.empty());

		ResponseEntity<TransactionDTO> response = controller.getTransactionById(2L);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void testCreateOrUpdateTransactionExisting() {
		TransactionDTO dto = new TransactionDTO();
		dto.setId(5L);
		TransactionDTO saved = new TransactionDTO();

		when(service.findById(5L)).thenReturn(Optional.of(new TransactionDTO()));
		when(service.save(dto)).thenReturn(saved);

		TransactionDTO result = controller.createOrUpdateTransaction(dto);

		assertEquals(saved, result);
		assertEquals(5L, dto.getId());
		verify(service).save(dto);
	}

	@Test
	void testCreateOrUpdateTransactionNew() {
		TransactionDTO dto = new TransactionDTO();
		dto.setId(5L);
		TransactionDTO saved = new TransactionDTO();

		when(service.findById(5L)).thenReturn(Optional.empty());
		when(service.save(any())).thenReturn(saved);

		TransactionDTO result = controller.createOrUpdateTransaction(dto);

		assertEquals(saved, result);
		ArgumentCaptor<TransactionDTO> captor = ArgumentCaptor.forClass(TransactionDTO.class);
		verify(service).save(captor.capture());
		assertNull(captor.getValue().getId());
	}

	@Test
	void testDeleteTransaction() {
		ResponseEntity<Void> response = controller.deleteTransaction(9L);

		verify(service).deleteById(9L);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		assertNull(response.getBody());
	}
}
