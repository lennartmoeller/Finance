import createPersistentStore from "@/utils/store/createPersistentStore";
import YearMonth from "@/utils/YearMonth";

type TransactionFilterStateData = {
    accountIds: Array<number>;
    categoryIds: Array<number>;
    yearMonths: Array<YearMonth>;
};

type TransactionFilterStateDataSerialized = {
    accountIds?: string;
    categoryIds?: string;
    yearMonths?: string;
};

type TransactionFilterState = TransactionFilterStateData & {
    setAccountIds: (accounts: Array<number>) => void;
    setCategoryIds: (categories: Array<number>) => void;
    setYearMonths: (yearMonths: Array<YearMonth>) => void;
};

const useTransactionFilter = createPersistentStore<
    TransactionFilterState,
    TransactionFilterStateData,
    TransactionFilterStateDataSerialized
>({
    name: "transactionFilter",
    stateCreator: (set) => ({
        accountIds: [],
        categoryIds: [],
        yearMonths: [YearMonth.fromDate(new Date())],
        setAccountIds: (accountIds) => set({ accountIds }),
        setCategoryIds: (categoryIds) => set({ categoryIds }),
        setYearMonths: (yearMonths) => set({ yearMonths }),
    }),
    storage: {
        storeInUrl: true,
        storeInLocalStorage: true,
        serialize: (
            state: TransactionFilterStateData,
        ): TransactionFilterStateDataSerialized => ({
            accountIds:
                state.accountIds.length > 0
                    ? state.accountIds.join(",")
                    : undefined,
            categoryIds:
                state.categoryIds.length > 0
                    ? state.categoryIds.join(",")
                    : undefined,
            yearMonths:
                state.yearMonths.length > 0
                    ? state.yearMonths.map(YearMonth.toString).join(",")
                    : undefined,
        }),
        parse: (
            packedValue: TransactionFilterStateDataSerialized,
        ): TransactionFilterStateData => ({
            accountIds: packedValue.accountIds?.split(",").map(Number) ?? [],
            categoryIds: packedValue.categoryIds?.split(",").map(Number) ?? [],
            yearMonths:
                packedValue.yearMonths?.split(",").map(YearMonth.fromString) ??
                [],
        }),
    },
});

export default useTransactionFilter;
