import styled from "styled-components";

const StyledCellContent = styled.div<{
    $horAlign?: 'left' | 'center' | 'right',
    $vertAlign?: 'top' | 'center' | 'bottom',
}>`
    box-sizing: border-box;
    padding: 2px 6px;
    display: grid;
    justify-content: ${({$horAlign}) => $horAlign ?? 'left'};
    align-content: ${({$vertAlign}) => $vertAlign ?? 'center'};
`;

export default StyledCellContent;
