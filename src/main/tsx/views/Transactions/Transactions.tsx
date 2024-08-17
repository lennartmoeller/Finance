import React from "react";

import useCachedGetQuery from "@/hooks/useCachedGetQuery";
import Account, {AccountDTO, accountMapper} from "@/types/Account";
import Category, {CategoryDTO, categoryMapper} from "@/types/Category";
import Transaction, {TransactionDTO, transactionMapper} from "@/types/Transaction";
import TransactionsTable from "@/views/Transactions/Table/TransactionsTable";

const Transactions: React.FC = () => {
    const {
        data: accounts,
        error: accountsError,
        isLoading: accountsIsLoading,
    } = useCachedGetQuery<Array<AccountDTO>, Array<Account>>('accounts', as => as.map(accountMapper.fromDTO));
    const {
        data: categories,
        error: categoriesError,
        isLoading: categoriesIsLoading,
    } = useCachedGetQuery<Array<CategoryDTO>, Array<Category>>('categories', cs => cs.map(categoryMapper.fromDTO));
    const {
        data: transactions,
        error: transactionsError,
        isLoading: transactionsIsLoading,
    } = useCachedGetQuery<Array<TransactionDTO>, Array<Transaction>>('transactions', ts => ts.map(transactionMapper.fromDTO));

    if (accountsIsLoading || categoriesIsLoading || transactionsIsLoading) return <div>Loading...</div>;
    if (accountsError) return <div>Error: {accountsError.message}</div>;
    if (categoriesError) return <div>Error: {categoriesError.message}</div>;
    if (transactionsError) return <div>Error: {transactionsError.message}</div>;
    if (!accounts || !categories || !transactions) return <div>No data available</div>;

    return (
        <TransactionsTable accounts={accounts} categories={categories} transactions={transactions}/>
    );

};

export default Transactions;
