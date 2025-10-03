import React from "react";

import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import Table from "@/components/Table/Table";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
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

    return (
        <StyledTransactionTable>
            <Table
                columnWidths={[98, 140, 200, 350, 100, 31]}
                stickyHeaderRows={1}
                rows={[
                    {
                        key: "header",
                        content: (
                            <>
                                <TableHeaderCell>Date</TableHeaderCell>
                                <TableHeaderCell>Account</TableHeaderCell>
                                <TableHeaderCell>Category</TableHeaderCell>
                                <TableHeaderCell>Description</TableHeaderCell>
                                <TableHeaderCell horAlign="center">
                                    Amount
                                </TableHeaderCell>
                                <TableHeaderCell />
                            </>
                        ),
                    },
                    {
                        key: (transaction: Transaction) => transaction.id,
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
                            onFocus: () => setFocusedTransaction(transaction),
                            onBlur: () => setFocusedTransaction(null),
                        }),
                    },
                    {
                        key: "draft",
                        content: (
                            <TransactionsTableRow
                                transaction={emptyTransaction}
                                accountInputFormatter={
                                    accountsSelectorInputFormatter
                                }
                                categoryInputFormatter={
                                    categoriesSelectorInputFormatter
                                }
                                draft
                            />
                        ),
                    },
                ]}
            />
        </StyledTransactionTable>
    );
};

export default TransactionsTable;
