import { Strategy } from "@floating-ui/react";
import styled from "styled-components";

const StyledTooltipContainer = styled.div<{
    $position: Strategy;
    $top?: number;
    $left?: number;
}>`
    position: ${({ $position }) => $position};
    top: ${({ $top }) => $top ?? 0}px;
    left: ${({ $left }) => $left ?? 0}px;
    z-index: 2;
    background: rgba(0, 0, 0, 0.75);
    padding: 5px 10px;
    border-radius: 4px;
    pointer-events: none;
    color: white;
    font-size: 12px;
`;

export default StyledTooltipContainer;
