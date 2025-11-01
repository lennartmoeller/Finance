import React, { useMemo } from "react";

import GermanYearMonthInputFormatter from "@/components/Form/InputFormatter/GermanYearMonthInputFormatter";
import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import Table from "@/components/Table/Table";
import Account from "@/types/Account";
import Category from "@/types/Category";
import Transaction, { emptyTransaction } from "@/types/Transaction";
import { filterDuplicates } from "@/utils/array";
import useFocusedTransaction from "@/views/TrackingView/stores/useFocusedTransaction";
import useTransactionFilter from "@/views/TrackingView/stores/useTransactionFilter";
import StyledTransactionTable from "@/views/TrackingView/TransactionsTable/styles/StyledTransactionTable";
import TransactionsTableRow from "@/views/TrackingView/TransactionsTable/TransactionsTableRow";
import TransactionFilters from "@/views/TrackingView/types/TransactionFilters";

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
    const {
        accountIds,
        categoryIds,
        yearMonths,
        setAccountIds,
        setCategoryIds,
        setYearMonths,
    } = useTransactionFilter();

    // Helper to convert single value or null to array
    const toArrayOrEmpty = <T,>(value: T | null | undefined): T[] =>
        value === null || value === undefined ? [] : [value];

    // Find non-leaf categories for filtering
    const leafCategories = useMemo(() => {
        const nonLeaf = categories
            .map((category) => category.parentId)
            .filter((parentId): parentId is number => parentId !== null);
        const nonLeafUnique = filterDuplicates(nonLeaf);
        return categories.filter(
            (category) => !nonLeafUnique.includes(category.id),
        );
    }, [categories]);

    const currentYear = useMemo(() => new Date().getFullYear(), []);

    const accountsSelectorInputFormatter = new SelectorInputFormatter({
        options: accounts,
        idProperty: "id",
        labelProperty: "label",
        required: true,
    });
    const categoriesSelectorInputFormatter = new SelectorInputFormatter({
        options: leafCategories,
        idProperty: "id",
        labelProperty: "label",
        required: true,
    });

    // Filter input formatters (not required)
    const yearMonthFilterInputFormatter = new GermanYearMonthInputFormatter({
        defaultYear: currentYear,
    });
    const accountsFilterInputFormatter = new SelectorInputFormatter({
        options: accounts,
        idProperty: "id",
        labelProperty: "label",
    });
    const categoriesFilterInputFormatter = new SelectorInputFormatter({
        options: leafCategories,
        idProperty: "id",
        labelProperty: "label",
    });

    const columns = [
        {
            key: "date",
            width: 98,
            header: { name: "Date", props: { horAlign: "center" as const } },
            filter: {
                property: "yearMonths",
                inputFormatter: yearMonthFilterInputFormatter,
            },
        },
        {
            key: "account",
            width: 140,
            header: { name: "Account" },
            filter: {
                property: "accountIds",
                inputFormatter: accountsFilterInputFormatter,
            },
        },
        {
            key: "category",
            width: 200,
            header: { name: "Category" },
            filter: {
                property: "categoryIds",
                inputFormatter: categoriesFilterInputFormatter,
            },
        },
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

    const handleFilterChange = (filters: TransactionFilters) => {
        // Update store with typed values
        setAccountIds(toArrayOrEmpty(filters.accountIds));
        setCategoryIds(toArrayOrEmpty(filters.categoryIds));
        setYearMonths(toArrayOrEmpty(filters.yearMonths));
    };

    const initialFilters = useMemo(
        () => ({
            accountIds: accountIds.length > 0 ? accountIds[0] : null,
            categoryIds: categoryIds.length > 0 ? categoryIds[0] : null,
            yearMonths: yearMonths.length > 0 ? yearMonths[0] : null,
        }),
        [accountIds, categoryIds, yearMonths],
    );

    return (
        <StyledTransactionTable>
            <Table
                columns={columns}
                stickyHeaderRows={1}
                rows={rows}
                onFilterChange={handleFilterChange}
                initialFilterValues={initialFilters}
            />
        </StyledTransactionTable>
    );
};

export default TransactionsTable;
