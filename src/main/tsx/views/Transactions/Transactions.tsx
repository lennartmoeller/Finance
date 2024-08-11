import React from "react";

import useGetQuery from "@/hooks/useGetQuery";
import Transaction, {TransactionDTO, transactionMapper} from "@/types/Transaction";
import TransactionsTable from "@/views/Transactions/Table/TransactionsTable";

const Transactions: React.FC = () => {
    const {
        data: transactions,
        error,
        isLoading
    } = useGetQuery<Array<TransactionDTO>, Array<Transaction>>('transactions', ts => ts.map(transactionMapper.fromDTO));

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;
    if (!transactions) return <div>No data available</div>;

    return (
        <TransactionsTable transactions={transactions}/>
    );

};

export default Transactions;
