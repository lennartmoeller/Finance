import React from "react";

import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import Table from "@/components/Table/Table";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
import TableRow from "@/components/Table/TableRow";
import Account from "@/types/Account";
import Category from "@/types/Category";
import Transaction, {emptyTransaction} from "@/types/Transaction";
import {filterDuplicates} from "@/utils/array";
import StyledTransactionTable from "@/views/TrackingView/TransactionsTable/styles/StyledTransactionTable";
import TransactionsTableRow from "@/views/TrackingView/TransactionsTable/TransactionsTableRow";

interface TransactionsTableProps {
    accounts: Account[];
    categories: Category[];
    transactions: Transaction[];
}

const TransactionsTable: React.FC<TransactionsTableProps> = (
    {
        accounts,
        categories,
        transactions,
    }: TransactionsTableProps
) => {

    const accountsSelectorInputFormatter = new SelectorInputFormatter({
        options: accounts,
        idProperty: "id",
        labelProperty: "label",
        required: true
    });
    const categoriesSelectorInputFormatter = new SelectorInputFormatter({
        options: (() => {
            // find non-leaf nodes
            const nonLeaf: number[] = categories
                .map((category: Category) => category.parentId)
                .filter((parentId: number | null) => parentId !== null);
            const nonLeafUnique: number[] = filterDuplicates(nonLeaf);
            // return only leaf nodes as options
            return categories.filter((category: Category) => !nonLeafUnique.includes(category.id));
        })(),
        idProperty: "id",
        labelProperty: "label",
        required: true
    });

    return (
        <StyledTransactionTable>
            <Table
                data={transactions}
                header={(
                    <TableRow>
                        <TableHeaderCell sticky="top">Date</TableHeaderCell>
                        <TableHeaderCell sticky="top">Account</TableHeaderCell>
                        <TableHeaderCell sticky="top">Category</TableHeaderCell>
                        <TableHeaderCell sticky="top">Description</TableHeaderCell>
                        <TableHeaderCell sticky="top" horAlign="center">Amount</TableHeaderCell>
                        <TableHeaderCell sticky="top"></TableHeaderCell>
                    </TableRow>
                )}
                body={(transaction: Transaction) => (
                    <TransactionsTableRow
                        key={transaction.id}
                        transaction={transaction}
                        accountInputFormatter={accountsSelectorInputFormatter}
                        categoryInputFormatter={categoriesSelectorInputFormatter}
                    />
                )}
                postRow={(
                    <TransactionsTableRow
                        transaction={emptyTransaction}
                        accountInputFormatter={accountsSelectorInputFormatter}
                        categoryInputFormatter={categoriesSelectorInputFormatter}
                        draft
                    />
                )}
            />
        </StyledTransactionTable>
    );
};

export default TransactionsTable;
