import React from "react";

import useForm from "@/components/Form/hooks/useForm";
import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import StringInputFormatter from "@/components/Form/InputFormatter/StringInputFormatter";
import InputField from "@/components/InputField/InputField";
import Account from "@/types/Account";
import Category from "@/types/Category";
import YearMonth from "@/utils/YearMonth";
import useTransactionFilter from "@/views/TrackingView/stores/useTransactionFilter";
import StyledTransactionsTableFilters
    from "@/views/TrackingView/TransactionsTableFilters/styles/StyledTransactionsTableFilters";

interface TransactionsTableFiltersProps {
    accounts: Account[];
    categories: Category[];
}

const TransactionsTableFilters: React.FC<TransactionsTableFiltersProps> = (
    {
        accounts,
        categories,
    }: TransactionsTableFiltersProps
) => {
    const {
        accountIds,
        categoryIds,
        yearMonths,
        setAccountIds,
        setCategoryIds,
        setYearMonths,
        reinit
    } = useTransactionFilter();
    reinit();

    const register = useForm<{ accountIds: number, categoryIds: number, yearMonths: string, }>({
        initial: {
            accountIds: accountIds.length > 0 ? accountIds[0] : null,
            categoryIds: categoryIds.length > 0 ? categoryIds[0] : null,
            yearMonths: yearMonths.join(","),
        },
        onSuccess: async (filters) => {
            setAccountIds(filters.accountIds === null ? [] : [filters.accountIds]);
            setCategoryIds(filters.categoryIds === null ? [] : [filters.categoryIds]);
            setYearMonths(filters.yearMonths.split(",").filter(x => x).map(YearMonth.fromString));
        }
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

    const stringInputFormatter = new StringInputFormatter();

    return (
        <StyledTransactionsTableFilters>
            <InputField
                label="Months"
                width={90}
                {...register("yearMonths")}
                inputFormatter={stringInputFormatter}
            />
            <InputField
                label="Accounts"
                width={140}
                {...register("accountIds")}
                inputFormatter={accountsSelectorInputFormatter}
            />
            <InputField
                label="Categories"
                width={200}
                {...register("categoryIds")}
                inputFormatter={categoriesSelectorInputFormatter}
            />
        </StyledTransactionsTableFilters>
    );

};

export default TransactionsTableFilters;
