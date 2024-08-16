import styled from "styled-components";

const StyledCell = styled.div<{
    $headerLevel?: 1 | 2,
    $sticky?: 'top' | 'left' | 'topAndLeft',
    $width?: number,
    $zIndex?: number
}>`
    position: ${({$sticky}) => $sticky ? 'sticky' : 'relative'};
    width: ${({$width}) => $width ? `${$width}px` : 'auto'};
    top: ${({$sticky}) => ($sticky === 'top' || $sticky === 'topAndLeft' ? '0' : 'auto')};
    left: ${({$sticky}) => ($sticky === 'left' || $sticky === 'topAndLeft' ? '0' : 'auto')};
    z-index: ${({$zIndex}) => $zIndex ?? 'auto'};
    background-color: ${props => {
        const cellType: string = props.$headerLevel ? `header${props.$headerLevel}` : 'body';
        return props.theme.table[cellType].backgroundColor;
    }};
    border: ${props => `${props.theme.border.width}px solid ${props.theme.border.color}`};
    
    // border to make sticky cells look good
    &::before {
        position: absolute;
        content: '';
        top: ${props => `${-props.theme.border.width}px`};
        right: ${props => `${-props.theme.border.width}px`};
        bottom: ${props => `${-props.theme.border.width}px`};
        left: ${props => `${-props.theme.border.width}px`};
        border: ${props => `${props.theme.border.width}px solid ${props.theme.border.color}`};
    }
`;

export default StyledCell;
