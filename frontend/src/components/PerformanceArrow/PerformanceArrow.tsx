import React from "react";

import Icon from "@/components/Icon/Icon";
import StyledPerformanceArrow from "@/components/PerformanceArrow/styles/StyledPerformanceArrow";
import getPerformanceColor from "@/utils/performanceColor";

interface PerformanceArrowProps {
    performance: number | undefined;
}

const PerformanceArrow: React.FC<PerformanceArrowProps> = ({ performance }) => {
    return (
        <StyledPerformanceArrow $color={performance !== undefined ? getPerformanceColor(performance) : undefined}>
            <Icon
                id="fa-solid fa-arrow-down"
                color="white"
                opacity={performance !== undefined ? undefined : 0}
                rotation={performance !== undefined ? performance * -180 : undefined}
                size={10}
            />
        </StyledPerformanceArrow>
    );
};

export default PerformanceArrow;
