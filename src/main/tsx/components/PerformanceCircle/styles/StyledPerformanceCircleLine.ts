import styled from "styled-components";

interface StyledPerformanceCircleLineProps {
    $rotation: number;
    $translateY: number;
    $lineHeight: number;
    $lineWidth: number;
    $backgroundColor: string;
    $borderRadius: number;
}

const StyledPerformanceCircleLine = styled.div.attrs<StyledPerformanceCircleLineProps>(
    ({$backgroundColor, $rotation, $translateY}) => ({
        style: {
            backgroundColor: $backgroundColor,
            transform: `translate(-50%, -50%) rotate(${$rotation}deg) translate(0, -${$translateY}px)`,
        },
    })
)<StyledPerformanceCircleLineProps>`
    position: absolute;
    top: 50%;
    left: 50%;
    height: ${({$lineHeight}) => `${$lineHeight}px`};
    width: ${({$lineWidth}) => `${$lineWidth}px`};
    border-radius: ${({$borderRadius}) => `${$borderRadius}px`};
`;

export default StyledPerformanceCircleLine;
