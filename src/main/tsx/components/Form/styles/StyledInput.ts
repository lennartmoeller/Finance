import styled from "styled-components";

const StyledInput = styled.input<{
    $textAlign?: 'left' | 'center' | 'right',
}>`
    all: unset;
    z-index: 1;
    width: 100%;
    text-align: ${({ $textAlign }) => $textAlign || 'left'};
`;

export default StyledInput;
