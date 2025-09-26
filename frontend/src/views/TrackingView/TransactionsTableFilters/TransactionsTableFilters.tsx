import React from "react";

import useForm from "@/components/Form/hooks/useForm";
import GermanYearMonthInputFormatter from "@/components/Form/InputFormatter/GermanYearMonthInputFormatter";
import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import InputField from "@/components/InputField/InputField";
import Account from "@/types/Account";
import Category from "@/types/Category";
import YearMonth from "@/utils/YearMonth";
import useTransactionFilter from "@/views/TrackingView/stores/useTransactionFilter";
import StyledTransactionsTableFilters from "@/views/TrackingView/TransactionsTableFilters/styles/StyledTransactionsTableFilters";

interface TransactionsTableFiltersProps {
    accounts: Account[];
    categories: Category[];
}

const TransactionsTableFilters: React.FC<TransactionsTableFiltersProps> = ({
    accounts,
    categories,
}: TransactionsTableFiltersProps) => {
    const {
        accountIds,
        categoryIds,
        yearMonths,
        setAccountIds,
        setCategoryIds,
        setYearMonths,
        reinit,
    } = useTransactionFilter();
    reinit();

    const register = useForm<{
        accountIds: number;
        categoryIds: number;
        yearMonths: YearMonth;
    }>({
        initial: {
            accountIds: accountIds.length > 0 ? accountIds[0] : null,
            categoryIds: categoryIds.length > 0 ? categoryIds[0] : null,
            yearMonths: yearMonths.length > 0 ? yearMonths[0] : null,
        },
        onSuccess: async (filters) => {
            setAccountIds(
                filters.accountIds === null ? [] : [filters.accountIds],
            );
            setCategoryIds(
                filters.categoryIds === null ? [] : [filters.categoryIds],
            );
            setYearMonths(
                filters.yearMonths === null ? [] : [filters.yearMonths],
            );
        },
    });

    const accountsSelectorInputFormatter = new SelectorInputFormatter({
        options: accounts,
        idProperty: "id",
        labelProperty: "label",
    });
    const categoriesSelectorInputFormatter = new SelectorInputFormatter({
        options: categories,
        idProperty: "id",
        labelProperty: "label",
    });
    const yearMonthsSelectorInputFormatter = new GermanYearMonthInputFormatter({
        defaultYear: new Date().getFullYear(),
    });

    return (
        <StyledTransactionsTableFilters>
            <InputField
                label="Month"
                width={90}
                {...register("yearMonths")}
                inputFormatter={yearMonthsSelectorInputFormatter}
            />
            <InputField
                label="Account"
                width={140}
                {...register("accountIds")}
                inputFormatter={accountsSelectorInputFormatter}
            />
            <InputField
                label="Category"
                width={200}
                {...register("categoryIds")}
                inputFormatter={categoriesSelectorInputFormatter}
            />
        </StyledTransactionsTableFilters>
    );
};

export default TransactionsTableFilters;
