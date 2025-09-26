import React from "react";

import { create } from "zustand/index";

interface HeaderStateData {
    headline: string | null;
    actions: React.ReactNode;
}

type HeaderState = HeaderStateData & {
    setHeader: (state: Partial<HeaderStateData>) => void;
};

const useHeader = create<HeaderState>((set) => ({
    headline: null,
    actions: null,
    setHeader: (state: Partial<HeaderStateData>) =>
        set({
            headline: state.headline ?? null,
            actions: state.actions ?? null,
        }),
}));

export default useHeader;
