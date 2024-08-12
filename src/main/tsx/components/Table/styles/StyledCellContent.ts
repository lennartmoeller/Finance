import styled from "styled-components";

const StyledCellContent = styled.div<{
    $headerLevel?: 1 | 2,
    $horAlign?: 'left' | 'center' | 'right',
    $vertAlign?: 'top' | 'center' | 'bottom',
}>`
    box-sizing: border-box;
    padding: 2px 6px;
    display: flex;
    justify-content: ${({$horAlign}) => {
        switch ($horAlign) {
            case 'center':
                return 'center';
            case 'right':
                return 'flex-end';
            default:
                return 'flex-start';
        }
    }};
    align-items: ${({$vertAlign}) => {
        switch ($vertAlign) {
            case 'center':
                return 'center';
            case 'bottom':
                return 'flex-end';
            default:
                return 'flex-start';
        }
    }};
    width: 100%;
    font-weight: ${props => {
        const cellType: string = props.$headerLevel ? `header${props.$headerLevel}` : 'body';
        return props.theme.table[cellType].fontWeight;
    }};
    font-size: ${props => `${props.theme.fontSize}px`};
`;

export default StyledCellContent;
