import {create} from "zustand";
import {persist} from "zustand/middleware";

import createStorage from "@/stores/util/createStorage";
import YearMonth from "@/utils/YearMonth";

type SelectedYearMonthState<T extends (YearMonth | string)> = {
    selectedYearMonth: T;
    previousMonth: () => void;
    nextMonth: () => void;
}

const selectedYearMonthStore = create<SelectedYearMonthState<YearMonth>>()(
    persist(
        (set, get) => ({
            selectedYearMonth: YearMonth.fromDate(new Date()),
            previousMonth: () => set({selectedYearMonth: get().selectedYearMonth.previous()}),
            nextMonth: () => set({selectedYearMonth: get().selectedYearMonth.next()}),
        }),
        {
            name: 'selectedYearMonth',
            storage: createStorage(
                (state: SelectedYearMonthState<YearMonth>): SelectedYearMonthState<string> => ({
                    ...state,
                    selectedYearMonth: YearMonth.toString(state.selectedYearMonth),
                }),
                (state: SelectedYearMonthState<string>): SelectedYearMonthState<YearMonth> => ({
                    ...state,
                    selectedYearMonth: YearMonth.fromString(state.selectedYearMonth),
                }),
            ),
        },
    ),
);

export default selectedYearMonthStore;
