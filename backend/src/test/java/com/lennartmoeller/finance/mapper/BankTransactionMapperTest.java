package com.lennartmoeller.finance.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import com.lennartmoeller.finance.repository.AccountRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class BankTransactionMapperTest {

    @Test
    void testIngV1ToEntity() {
        IngV1TransactionDTO dto = new IngV1TransactionDTO();
        dto.setIban("DE123");
        dto.setBookingDate(LocalDate.of(2024, 1, 1));
        dto.setPurpose("p");
        dto.setCounterparty("c");
        dto.setAmount(100L);
        dto.setData(new java.util.HashMap<>());
        dto.getData().put("a", "b");

        AccountRepository repo = mock(AccountRepository.class);
        Account acc = new Account();
        when(repo.findByIban("DE123")).thenReturn(java.util.Optional.of(acc));

        BankTransactionMapper mapper = new BankTransactionMapperImpl();
        BankTransaction entity = mapper.toEntity(dto, repo);

        assertSame(acc, entity.getAccount());
        assertEquals(dto.getBookingDate(), entity.getBookingDate());
        assertEquals(dto.getPurpose(), entity.getPurpose());
        assertEquals(dto.getCounterparty(), entity.getCounterparty());
        assertEquals(dto.getAmount(), entity.getAmount());
        assertEquals("ING_V1", entity.getBank().name());
        assertEquals(dto.getData(), entity.getData());
    }

    @Test
    void testCamtV8ToEntity() {
        CamtV8TransactionDTO dto = new CamtV8TransactionDTO();
        dto.setIban("DE456");
        dto.setBookingDate(LocalDate.of(2024, 2, 2));
        dto.setPurpose("p2");
        dto.setCounterparty("c2");
        dto.setAmount(200L);
        dto.setData(new java.util.HashMap<>());
        dto.getData().put("x", "y");

        AccountRepository repo = mock(AccountRepository.class);
        Account acc = new Account();
        when(repo.findByIban("DE456")).thenReturn(java.util.Optional.of(acc));

        BankTransactionMapper mapper = new BankTransactionMapperImpl();
        BankTransaction entity = mapper.toEntity(dto, repo);

        assertEquals("CAMT_V8", entity.getBank().name());
        assertSame(acc, entity.getAccount());
        assertEquals(dto.getData(), entity.getData());
    }

    @Test
    void testToDto() {
        BankTransaction entity = new BankTransaction();
        Account account = new Account();
        account.setIban("DE123");
        entity.setAccount(account);
        entity.setId(3L);
        entity.setBank(BankType.ING_V1);
        entity.setBookingDate(LocalDate.of(2024, 3, 3));
        entity.setPurpose("p");
        entity.setCounterparty("c");
        entity.setAmount(10L);
        entity.getData().put("k", "v");

        BankTransactionDTO dto = new BankTransactionMapperImpl().toDto(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(account.getIban(), dto.getIban());
        assertEquals(entity.getData(), dto.getData());
    }

    @Test
    void testBaseToEntity() {
        BankTransactionDTO dto = new BankTransactionDTO();
        dto.setId(5L);
        dto.setBank(BankType.CAMT_V8);
        dto.setIban("DE");
        dto.setBookingDate(LocalDate.of(2024, 4, 4));
        dto.setPurpose("p");
        dto.setCounterparty("c");
        dto.setAmount(50L);
        dto.setData(new java.util.HashMap<>());
        dto.getData().put("k", "v");

        AccountRepository repo = mock(AccountRepository.class);
        Account acc = new Account();
        when(repo.findByIban("DE")).thenReturn(java.util.Optional.of(acc));

        BankTransaction entity = new BankTransactionMapperImpl().toEntity(dto, repo);
        assertEquals(dto.getBank(), entity.getBank());
        assertSame(acc, entity.getAccount());
        assertEquals(dto.getData(), entity.getData());
    }

    @Test
    void nullInputsReturnNull() {
        BankTransactionMapper mapper = new BankTransactionMapperImpl();
        AccountRepository repo = mock(AccountRepository.class);
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity((BankTransactionDTO) null, repo));
        assertNull(mapper.toEntity((IngV1TransactionDTO) null, repo));
        assertNull(mapper.toEntity((CamtV8TransactionDTO) null, repo));
    }

    @Test
    void handlesNullDataFields() {
        BankTransaction entity = new BankTransaction();
        entity.setData(null);
        BankTransactionDTO dto = new BankTransactionMapperImpl().toDto(entity);
        assertNull(dto.getData());

        BankTransactionDTO base = new BankTransactionDTO();
        base.setData(null);
        BankTransaction mapped = new BankTransactionMapperImpl().toEntity(base, mock(AccountRepository.class));
        assertTrue(mapped.getData().isEmpty());

        IngV1TransactionDTO ing = new IngV1TransactionDTO();
        ing.setData(null);
        assertTrue(new BankTransactionMapperImpl()
                .toEntity(ing, mock(AccountRepository.class))
                .getData()
                .isEmpty());

        CamtV8TransactionDTO camt = new CamtV8TransactionDTO();
        camt.setData(null);
        assertTrue(new BankTransactionMapperImpl()
                .toEntity(camt, mock(AccountRepository.class))
                .getData()
                .isEmpty());
    }

    @Test
    void testMappingHelper() throws Exception {
        BankTransactionMapperImpl mapper = new BankTransactionMapperImpl();
        AccountRepository repo = mock(AccountRepository.class);
        Account account = new Account();
        when(repo.findByIban("IBAN")).thenReturn(java.util.Optional.of(account));

        java.lang.reflect.Method helper = BankTransactionMapper.class.getDeclaredMethod(
                "mapIbanToAccount", String.class, AccountRepository.class);
        helper.setAccessible(true);

        assertSame(account, helper.invoke(mapper, "IBAN", repo));
        assertNull(helper.invoke(mapper, null, repo));
        when(repo.findByIban("MISSING")).thenReturn(java.util.Optional.empty());
        assertNull(helper.invoke(mapper, "MISSING", repo));
    }
}
