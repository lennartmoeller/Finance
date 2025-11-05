import React from "react";

import StyledPerformanceCircleContainer from "@/components/PerformanceCircle/styles/StyledPerformanceCircleContainer";
import StyledPerformanceCircleLine from "@/components/PerformanceCircle/styles/StyledPerformanceCircleLine";
import StyledPerformanceCircleNumber from "@/components/PerformanceCircle/styles/StyledPerformanceCircleNumber";
import getPerformanceColor from "@/utils/performanceColor";

interface PerformanceCircleProps {
    performance: number;
    size?: number;
}

const PerformanceCircle: React.FC<PerformanceCircleProps> = ({ performance, size = 50 }) => {
    const lineHeight = size / 8;
    const lineWidth = 2;
    const totalLines = Math.ceil((Math.PI * (size - lineHeight)) / (2 * lineWidth));
    const translateY = size / 2 - lineHeight / 2;
    const percentage = Math.round(performance * 100);
    const activeColor = getPerformanceColor(performance);

    return (
        <StyledPerformanceCircleContainer $size={size}>
            {Array.from({ length: totalLines }).map((_, index) => {
                const rotation = (360 / totalLines) * index;
                const backgroundColor = index < totalLines * performance ? activeColor : "rgba(0, 0, 0, 0.1)";
                return (
                    <StyledPerformanceCircleLine
                        key={index}
                        $rotation={rotation}
                        $translateY={translateY}
                        $lineHeight={lineHeight}
                        $lineWidth={lineWidth}
                        $backgroundColor={backgroundColor}
                        $borderRadius={lineWidth / 3}
                    />
                );
            })}
            <StyledPerformanceCircleNumber>{percentage}</StyledPerformanceCircleNumber>
        </StyledPerformanceCircleContainer>
    );
};

export default PerformanceCircle;
