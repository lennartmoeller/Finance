import { create } from "zustand";

import Transaction from "@/types/Transaction";

type FocusedTransactionState = {
    focusedTransaction: Transaction | null;
    setFocusedTransaction: (transaction: Transaction | null) => void;
};

const useFocusedTransaction = create<FocusedTransactionState>((set) => ({
    focusedTransaction: null,
    setFocusedTransaction: (focusedTransaction) => set({ focusedTransaction }),
}));

export default useFocusedTransaction;
