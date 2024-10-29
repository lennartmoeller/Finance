import React from "react";

import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import Table from "@/components/Table/Table";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
import TableRow from "@/components/Table/TableRow";
import {useAccounts} from "@/services/accounts";
import {useCategories} from "@/services/categories";
import {useTransactions} from "@/services/transactions";
import Category from "@/types/Category";
import Transaction, {emptyTransaction} from "@/types/Transaction";
import {filterDuplicates} from "@/utils/array";
import TransactionsTableRow from "@/views/TrackingView/TransactionsTable/TransactionsTableRow";

const TransactionsTable: React.FC = () => {
    const accounts = useAccounts();
    const categories = useCategories();
    const transactions = useTransactions();

    if (accounts.isLoading || categories.isLoading || transactions.isLoading) return (
        <div>Loading...</div>
    );
    if (accounts.error || categories.error || transactions.error) return (
        <div>Error: {accounts.error?.message ?? categories.error?.message ?? transactions.error?.message}</div>
    );
    if (!accounts.data || !categories.data || !transactions.data) return (
        <div>No data available</div>
    );

    const accountsSelectorInputFormatter = new SelectorInputFormatter({
        options: accounts.data,
        idProperty: "id",
        labelProperty: "label",
        required: true
    });
    const categoriesSelectorInputFormatter = new SelectorInputFormatter({
        options: (() => {
            // find non-leaf nodes
            const nonLeaf: number[] = categories.data
                .map((category: Category) => category.parentId)
                .filter((parentId: number | null) => parentId !== null);
            const nonLeafUnique: number[] = filterDuplicates(nonLeaf);
            // return only leaf nodes as options
            return categories.data.filter((category: Category) => !nonLeafUnique.includes(category.id));
        })(),
        idProperty: "id",
        labelProperty: "label",
        required: true
    });

    return (
        <Table
            data={transactions.data}
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
    );
};

export default TransactionsTable;
