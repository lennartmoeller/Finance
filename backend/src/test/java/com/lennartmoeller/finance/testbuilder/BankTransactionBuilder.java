package com.lennartmoeller.finance.testbuilder;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import java.time.LocalDate;

public final class BankTransactionBuilder {
    private final BankTransaction tx = new BankTransaction();

    private BankTransactionBuilder() {}

    public static BankTransactionBuilder aBankTransaction() {
        return new BankTransactionBuilder();
    }

    public BankTransactionBuilder withAccount(Account account) {
        tx.setAccount(account);
        return this;
    }

    public BankTransactionBuilder withBookingDate(LocalDate date) {
        tx.setBookingDate(date);
        return this;
    }

    public BankTransactionBuilder withPurpose(String purpose) {
        tx.setPurpose(purpose);
        return this;
    }

    public BankTransactionBuilder withCounterparty(String counterparty) {
        tx.setCounterparty(counterparty);
        return this;
    }

    public BankTransactionBuilder withAmount(Long amount) {
        tx.setAmount(amount);
        return this;
    }

    public BankTransactionBuilder withData(String data) {
        tx.setData(data);
        return this;
    }

    public BankTransaction build() {
        return tx;
    }
}
