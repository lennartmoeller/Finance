import React from "react";

import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import Table from "@/components/Table/Table";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
import TableRow from "@/components/Table/TableRow";
import Account from "@/types/Account";
import Category from "@/types/Category";
import Transaction, { emptyTransaction } from "@/types/Transaction";
import { filterDuplicates } from "@/utils/array";
import { Nullable } from "@/utils/types";
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

    return (
        <StyledTransactionTable>
            <Table
                header={
                    <TableRow>
                        <TableHeaderCell width={98} sticky="top">
                            Date
                        </TableHeaderCell>
                        <TableHeaderCell width={140} sticky="top">
                            Account
                        </TableHeaderCell>
                        <TableHeaderCell width={200} sticky="top">
                            Category
                        </TableHeaderCell>
                        <TableHeaderCell width={350} sticky="top">
                            Description
                        </TableHeaderCell>
                        <TableHeaderCell
                            width={100}
                            sticky="top"
                            horAlign="center"
                        >
                            Amount
                        </TableHeaderCell>
                        <TableHeaderCell
                            width={31}
                            sticky="top"
                        ></TableHeaderCell>
                    </TableRow>
                }
                body={{
                    data: transactions,
                    content: (transaction: Transaction) => (
                        <TransactionsTableRow
                            transaction={transaction}
                            accountInputFormatter={
                                accountsSelectorInputFormatter
                            }
                            categoryInputFormatter={
                                categoriesSelectorInputFormatter
                            }
                        />
                    ),
                    properties: (transaction: Transaction) => ({
                        onFocus: () => {
                            setFocusedTransaction(transaction);
                        },
                        onBlur: () => {
                            setFocusedTransaction(null);
                        },
                    }),
                }}
                post={{
                    data: [emptyTransaction],
                    content: (transaction: Nullable<Transaction>) => (
                        <TransactionsTableRow
                            transaction={transaction}
                            accountInputFormatter={
                                accountsSelectorInputFormatter
                            }
                            categoryInputFormatter={
                                categoriesSelectorInputFormatter
                            }
                            draft
                        />
                    ),
                    properties: () => ({}),
                }}
            />
        </StyledTransactionTable>
    );
};

export default TransactionsTable;
