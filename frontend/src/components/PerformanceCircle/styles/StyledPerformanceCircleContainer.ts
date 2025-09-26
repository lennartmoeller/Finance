import styled from "styled-components";

const StyledPerformanceCircleContainer = styled.div<{ $size: number }>`
    position: relative;
    width: ${({ $size }) => `${$size}px`};
    height: ${({ $size }) => `${$size}px`};
`;

export default StyledPerformanceCircleContainer;
