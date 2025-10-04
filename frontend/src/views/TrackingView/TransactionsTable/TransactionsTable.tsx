import React from "react";

import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import Table from "@/components/Table/Table";
import Account from "@/types/Account";
import Category from "@/types/Category";
import Transaction, { emptyTransaction } from "@/types/Transaction";
import { filterDuplicates } from "@/utils/array";
import useFocusedTransaction from "@/views/TrackingView/stores/useFocusedTransaction";
import StyledTransactionTable from "@/views/TrackingView/TransactionsTable/styles/StyledTransactionTable";
import TransactionsTableRow from "@/views/TrackingView/TransactionsTable/TransactionsTableRow";

interface TransactionsTableProps {
    accounts: Account[];
    categories: Category[];
    transactions: Transaction[];
}

const TransactionsTable: React.FC<TransactionsTableProps> = ({
    accounts,
    categories,
    transactions,
}: TransactionsTableProps) => {
    const setFocusedTransaction = useFocusedTransaction(
        (state) => state.setFocusedTransaction,
    );
    const accountsSelectorInputFormatter = new SelectorInputFormatter({
        options: accounts,
        idProperty: "id",
        labelProperty: "label",
        required: true,
    });
    const categoriesSelectorInputFormatter = new SelectorInputFormatter({
        options: (() => {
            // find non-leaf nodes
            const nonLeaf: number[] = categories
                .map((category: Category) => category.parentId)
                .filter((parentId: number | null) => parentId !== null);
            const nonLeafUnique: number[] = filterDuplicates(nonLeaf);
            // return only leaf nodes as options
            return categories.filter(
                (category: Category) => !nonLeafUnique.includes(category.id),
            );
        })(),
        idProperty: "id",
        labelProperty: "label",
        required: true,
    });

    const columns = [
        {
            key: "date",
            width: 98,
            header: { name: "Date", props: { horAlign: "center" as const } },
        },
        { key: "account", width: 140, header: { name: "Account" } },
        { key: "category", width: 200, header: { name: "Category" } },
        { key: "description", width: 350, header: { name: "Description" } },
        {
            key: "amount",
            width: 100,
            header: { name: "Amount", props: { horAlign: "center" as const } },
        },
        { key: "actions", width: 31, header: { name: "" } },
    ];

    const rows = [
        {
            key: (transaction: Transaction) => transaction.id,
            data: transactions,
            content: (transaction: Transaction) => (
                <TransactionsTableRow
                    transaction={transaction}
                    accountInputFormatter={accountsSelectorInputFormatter}
                    categoryInputFormatter={categoriesSelectorInputFormatter}
                />
            ),
            properties: (transaction: Transaction) => ({
                onFocus: () => setFocusedTransaction(transaction),
                onBlur: () => setFocusedTransaction(null),
            }),
        },
        {
            key: "draft",
            content: (
                <TransactionsTableRow
                    transaction={emptyTransaction}
                    accountInputFormatter={accountsSelectorInputFormatter}
                    categoryInputFormatter={categoriesSelectorInputFormatter}
                    draft
                />
            ),
        },
    ];

    return (
        <StyledTransactionTable>
            <Table columns={columns} stickyHeaderRows={1} rows={rows} />
        </StyledTransactionTable>
    );
};

export default TransactionsTable;
