import styled from "styled-components";

const StyledCellContent = styled.div<{
    $headerLevel?: 1 | 2,
    $horAlign?: 'left' | 'center' | 'right',
    $vertAlign?: 'top' | 'center' | 'bottom',
}>`
    box-sizing: border-box;
    padding: 2px 6px;
    display: grid;
    justify-content: ${({$horAlign}) => $horAlign ?? 'left'};
    align-content: ${({$vertAlign}) => $vertAlign ?? 'center'};
    font-weight: ${props => {
        const cellType: string = props.$headerLevel ? `header${props.$headerLevel}` : 'body';
        return props.theme.table[cellType].fontWeight;
    }};
    font-size: ${props => `${props.theme.fontSize}px`};
`;

export default StyledCellContent;
