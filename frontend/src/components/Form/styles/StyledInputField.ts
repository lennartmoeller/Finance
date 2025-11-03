import styled from "styled-components";

const StyledInputField = styled.input<{
    $textAlign?: "left" | "center" | "right";
    $reducedWidth?: number;
}>`
    all: unset;
    z-index: 1;
    width: ${({ $reducedWidth }) =>
        $reducedWidth ? `calc(100% - ${$reducedWidth}px)` : "100%"};
    box-sizing: border-box;
    text-align: ${({ $textAlign }) => $textAlign || "left"};
`;

export default StyledInputField;
