import styled from "styled-components";

const StyledInputFieldPlaceholder = styled.div<{
    $leftOffset?: number;
}>`
    position: absolute;
    top: 0;
    left: ${({ $leftOffset }) => ($leftOffset === undefined ? "auto" : `${$leftOffset}px`)};
    right: ${({ $leftOffset }) => ($leftOffset === undefined ? "0" : "auto")};
    bottom: 0;
    pointer-events: none;
    white-space: nowrap;
`;

export default StyledInputFieldPlaceholder;
