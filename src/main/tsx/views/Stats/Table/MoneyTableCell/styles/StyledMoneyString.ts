import styled from "styled-components";

const StyledMoneyString = styled.div<{
    $zero: boolean;
}>`
    font-family: monospace;
    font-size: 11px;
    opacity: ${({$zero}) => $zero ? 0.2 : 1};
`;

export default StyledMoneyString;
