import createPersistentStore from "@/utils/store/createPersistentStore";
import YearMonth from "@/utils/YearMonth";

type TransactionFilterStateData = {
    accountIds: Array<number>;
    categoryIds: Array<number>;
    yearMonths: Array<YearMonth>;
    description: string;
};

type TransactionFilterStateDataSerialized = {
    accountIds?: string;
    categoryIds?: string;
    yearMonths?: string;
    description?: string;
};

type TransactionFilterState = TransactionFilterStateData & {
    setAccountIds: (accounts: Array<number>) => void;
    setCategoryIds: (categories: Array<number>) => void;
    setYearMonths: (yearMonths: Array<YearMonth>) => void;
    setDescription: (description: string) => void;
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
        description: "",
        setAccountIds: (accountIds) => set({ accountIds }),
        setCategoryIds: (categoryIds) => set({ categoryIds }),
        setYearMonths: (yearMonths) => set({ yearMonths }),
        setDescription: (description) => set({ description }),
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
            description:
                state.description.length > 0 ? state.description : undefined,
        }),
        parse: (
            packedValue: TransactionFilterStateDataSerialized,
        ): TransactionFilterStateData => ({
            accountIds: packedValue.accountIds?.split(",").map(Number) ?? [],
            categoryIds: packedValue.categoryIds?.split(",").map(Number) ?? [],
            yearMonths:
                packedValue.yearMonths?.split(",").map(YearMonth.fromString) ??
                [],
            description: packedValue.description ?? "",
        }),
    },
});

export default useTransactionFilter;
