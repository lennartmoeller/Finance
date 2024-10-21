import {create} from "zustand";
import {persist} from "zustand/middleware";

import createUrlAndLocalStorage from "@/stores/util/createUrlAndLocalStorage";
import YearMonth from "@/utils/YearMonth";

type SelectedYearMonthStateData = {
    selectedYearMonth: YearMonth;
}

type SelectedYearMonthState = SelectedYearMonthStateData & {
    previousMonth: () => void;
    nextMonth: () => void;
}

const selectedYearMonthStore = create<SelectedYearMonthState>()(
    persist(
        (set, get) => ({
            selectedYearMonth: YearMonth.fromDate(new Date()),
            previousMonth: () => set({selectedYearMonth: get().selectedYearMonth.previous()}),
            nextMonth: () => set({selectedYearMonth: get().selectedYearMonth.next()}),
        }),
        {
            name: 'selectedYearMonth',
            storage: createUrlAndLocalStorage(
                (state: SelectedYearMonthStateData): string => YearMonth.toString(state.selectedYearMonth),
                (stringValue: string): SelectedYearMonthStateData => ({
                    selectedYearMonth: YearMonth.fromString(stringValue),
                }),
            ),
        },
    ),
);

export default selectedYearMonthStore;
