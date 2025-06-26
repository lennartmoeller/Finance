import styled from "styled-components";

const StyledCellContent = styled.div<{
    $headerLevel?: 1 | 2,
    $padding?: string;
    $horAlign?: 'left' | 'center' | 'right',
    $vertAlign?: 'top' | 'center' | 'bottom',
}>`
    padding: ${({$padding}) => $padding ?? "4px 8px"};
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
        const cellType: "header1" | "header2" | "body" = props.$headerLevel ? `header${props.$headerLevel}` : 'body';
        return props.theme.table[cellType].fontWeight;
    }};
    font-size: ${props => `${props.theme.fontSize}px`};
`;

export default StyledCellContent;
