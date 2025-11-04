import React, { useMemo } from "react";

import Fuse from "fuse.js";

import GermanYearMonthInputFormatter from "@/components/Form/InputFormatter/GermanYearMonthInputFormatter";
import MultiSelectorInputFormatter from "@/components/Form/InputFormatter/MultiSelectorInputFormatter";
import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import StringInputFormatter from "@/components/Form/InputFormatter/StringInputFormatter";
import Table from "@/components/Table/Table";
import Account from "@/types/Account";
import Category from "@/types/Category";
import Transaction, { emptyTransaction } from "@/types/Transaction";
import { ensureArray, filterDuplicates } from "@/utils/array";
import { expandCategoryIds } from "@/utils/categoryHierarchy";
import YearMonth from "@/utils/YearMonth";
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
        description,
        setAccountIds,
        setCategoryIds,
        setYearMonths,
        setDescription,
    } = useTransactionFilter();

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
    const accountsFilterInputFormatter = new MultiSelectorInputFormatter({
        options: accounts,
        idProperty: "id",
        labelProperty: "label",
    });
    const categoriesFilterInputFormatter = new MultiSelectorInputFormatter({
        options: leafCategories,
        idProperty: "id",
        labelProperty: "label",
    });
    const descriptionFilterInputFormatter = new StringInputFormatter();

    const columns = [
        {
            key: "date",
            width: 98,
            header: { name: "Date", props: { horAlign: "center" as const } },
            filter: {
                property: "yearMonths",
                inputFormatter: yearMonthFilterInputFormatter,
                filterFunction: (
                    yearMonths: YearMonth | YearMonth[],
                    transaction: Transaction,
                ) => {
                    const yearMonthsArray = Array.isArray(yearMonths)
                        ? yearMonths
                        : [yearMonths];

                    const transactionYearMonth = YearMonth.fromDate(
                        transaction.date,
                    );
                    return yearMonthsArray.some(
                        (ym) =>
                            ym.toString() === transactionYearMonth.toString(),
                    );
                },
            },
        },
        {
            key: "account",
            width: 140,
            header: { name: "Account" },
            filter: {
                property: "accountIds",
                inputFormatter: accountsFilterInputFormatter,
                filterFunction: (
                    accountIds: number[],
                    transaction: Transaction,
                ) => {
                    return accountIds.includes(transaction.accountId);
                },
            },
        },
        {
            key: "category",
            width: 200,
            header: { name: "Category" },
            filter: {
                property: "categoryIds",
                inputFormatter: categoriesFilterInputFormatter,
                filterFunction: (
                    categoryIds: number[],
                    transaction: Transaction,
                ) => {
                    const expandedCategoryIds = expandCategoryIds(
                        categoryIds,
                        categories,
                    );
                    if (!expandedCategoryIds) return true;

                    return expandedCategoryIds.includes(transaction.categoryId);
                },
            },
        },
        {
            key: "description",
            width: 350,
            header: { name: "Description" },
            filter: {
                property: "description",
                inputFormatter: descriptionFilterInputFormatter,
                filterFunction: (
                    searchString: string,
                    transaction: Transaction,
                ) => {
                    if (!searchString || searchString.trim().length === 0) {
                        return true;
                    }

                    const fuse = new Fuse([transaction], {
                        keys: ["description"],
                        includeScore: true,
                        threshold: 0.3,
                    });

                    const results = fuse.search(searchString);
                    return results.length > 0;
                },
            },
        },
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
        setAccountIds(ensureArray(filters.accountIds));
        setCategoryIds(ensureArray(filters.categoryIds));
        setYearMonths(ensureArray(filters.yearMonths));
        setDescription(filters.description ?? "");
    };

    const initialFilters = useMemo(
        () => ({
            accountIds: accountIds.length > 0 ? accountIds : null,
            categoryIds: categoryIds.length > 0 ? categoryIds : null,
            yearMonths: yearMonths.length > 0 ? yearMonths[0] : null,
            description: description.length > 0 ? description : null,
        }),
        [accountIds, categoryIds, yearMonths, description],
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
