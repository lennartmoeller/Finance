import React from "react";

import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import Table from "@/components/Table/Table";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
import TableRow from "@/components/Table/TableRow";
import Account from "@/types/Account";
import Category from "@/types/Category";
import Transaction from "@/types/Transaction";
import TransactionsTableRow from "@/views/Transactions/Table/TransactionsTableRow";

interface TransactionsTableProps {
    accounts: Array<Account>;
    categories: Array<Category>;
    transactions: Array<Transaction>;
}

const TransactionsTable: React.FC<TransactionsTableProps> = ({accounts, categories, transactions,}) => {
    const accountsSelectorInputFormatter = new SelectorInputFormatter(accounts, "id", "label");
    const categoriesSelectorInputFormatter = new SelectorInputFormatter(categories, "id", "label");

    return (
        <Table
            data={transactions}
            header={(
                <TableRow>
                    <TableHeaderCell>Date</TableHeaderCell>
                    <TableHeaderCell>Account</TableHeaderCell>
                    <TableHeaderCell>Category</TableHeaderCell>
                    <TableHeaderCell>Description</TableHeaderCell>
                    <TableHeaderCell horAlign="center">Amount</TableHeaderCell>
                </TableRow>
            )}
            body={(transaction: Transaction) => (
                <TransactionsTableRow
                    key={transaction.id}
                    transaction={transaction}
                    accountsSelectorInputFormatter={accountsSelectorInputFormatter}
                    categoriesSelectorInputFormatter={categoriesSelectorInputFormatter}
                />
            )}
        />
    );
};

export default TransactionsTable;
