import React from "react";

import Button from "@/components/Button/Button";
import Icon from "@/components/Icon/Icon";
import StyledYearMonthSelector from "@/components/YearMonthSelector/styles/StyledYearMonthSelector";
import StyledYearMonthSelectorLabel from "@/components/YearMonthSelector/styles/StyledYearMonthSelectorLabel";
import YearMonth from "@/utils/YearMonth";

export interface ButtonProps {
    value: YearMonth;
    previous: () => void;
    next: () => void;
}

const YearMonthSelector: React.FC<ButtonProps> = ({value, previous, next,}) => {
    return (
        <StyledYearMonthSelector>
            <Button onClick={previous}>
                <Icon id={"fa-solid fa-chevron-left"}/>
            </Button>
            <StyledYearMonthSelectorLabel>
                {value.toLabel()}
            </StyledYearMonthSelectorLabel>
            <Button onClick={next}>
                <Icon id={"fa-solid fa-chevron-right"}/>
            </Button>
        </StyledYearMonthSelector>
    );
};

export default YearMonthSelector;
