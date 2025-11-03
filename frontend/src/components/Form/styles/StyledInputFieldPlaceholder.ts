import styled from "styled-components";

const StyledInputFieldPlaceholder = styled.div<{
    $leftOffset?: number;
}>`
    position: absolute;
    top: 0;
    left: ${({ $leftOffset }) =>
        $leftOffset !== undefined ? `${$leftOffset}px` : "auto"};
    right: ${({ $leftOffset }) => ($leftOffset !== undefined ? "auto" : "0")};
    bottom: 0;
    pointer-events: none;
    white-space: nowrap;
`;

export default StyledInputFieldPlaceholder;
