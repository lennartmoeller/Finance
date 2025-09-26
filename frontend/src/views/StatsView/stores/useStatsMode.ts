import createPersistentStore from "@/utils/store/createPersistentStore";

type TransactionFilterStateData = {
    smoothed: boolean;
    merged: boolean;
};

type TransactionFilterStateDataSerialized = {
    smoothed: string;
    merged: string;
};

type TransactionFilterState = TransactionFilterStateData & {
    setSmoothed: (smoothed: boolean) => void;
    setMerged: (shared: boolean) => void;
};

const useStatsMode = createPersistentStore<
    TransactionFilterState,
    TransactionFilterStateData,
    TransactionFilterStateDataSerialized
>({
    name: "statsMode",
    stateCreator: (set) => ({
        smoothed: true,
        merged: false,
        setSmoothed: (smoothed) => set({ smoothed }),
        setMerged: (merged) => set({ merged }),
    }),
    storage: {
        storeInUrl: false,
        storeInLocalStorage: true,
        serialize: (
            state: TransactionFilterStateData,
        ): TransactionFilterStateDataSerialized => ({
            smoothed: state.smoothed ? "true" : "false",
            merged: state.merged ? "true" : "false",
        }),
        parse: (
            packedValue: TransactionFilterStateDataSerialized,
        ): TransactionFilterStateData => ({
            smoothed: packedValue.smoothed === "true",
            merged: packedValue.merged === "true",
        }),
    },
});

export default useStatsMode;
