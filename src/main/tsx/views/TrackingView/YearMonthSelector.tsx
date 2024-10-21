import React from "react";

import BaseYearMonthSelector from "@/components/YearMonthSelector/YearMonthSelector";
import selectedYearMonthStore from "@/stores/selectedYearMonthStore";

const YearMonthSelector: React.FC = () => {
    const {selectedYearMonth, previousMonth, nextMonth} = selectedYearMonthStore();

    return (
        <BaseYearMonthSelector value={selectedYearMonth} previous={previousMonth} next={nextMonth}/>
    );

};

export default YearMonthSelector;
