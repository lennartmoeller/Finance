import React from "react";

import Table from "@/components/Table/Table";
import TableBodyCell from "@/components/Table/TableBodyCell";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
import TableRow from "@/components/Table/TableRow";
import Transaction from "@/types/Transaction";

interface TransactionsTableProps {
    transactions: Array<Transaction>;
}

const TransactionsTable: React.FC<TransactionsTableProps> = ({transactions,}) => {
    return (
        <Table
            data={transactions}
            header={(
                <TableRow>
                    <TableHeaderCell>Date</TableHeaderCell>
                    <TableHeaderCell>Account</TableHeaderCell>
                    <TableHeaderCell>Category</TableHeaderCell>
                    <TableHeaderCell>Description</TableHeaderCell>
                    <TableHeaderCell>Amount</TableHeaderCell>
                </TableRow>
            )}
            body={(transaction: Transaction) => (
                <TableRow>
                    <TableBodyCell>{transaction.date.toDateString()}</TableBodyCell>
                    <TableBodyCell>{transaction.accountId}</TableBodyCell>
                    <TableBodyCell>{transaction.categoryId}</TableBodyCell>
                    <TableBodyCell>{transaction.description}</TableBodyCell>
                    <TableBodyCell>{transaction.amount}</TableBodyCell>
                </TableRow>
            )}
        />
    );
};

export default TransactionsTable;
