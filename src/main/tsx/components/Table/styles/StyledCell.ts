import styled from "styled-components";

export const StyledCell = styled.div<{
    $sticky?: 'top' | 'left' | 'topAndLeft',
    $width?: number,
    $zIndex?: number
}>`
    position: ${({$sticky}) => $sticky ? 'sticky' : 'relative'};
    width: ${({$width}) => $width ? `${$width}px` : 'auto'};
    padding: 0;
    top: ${({$sticky}) => ($sticky === 'top' || $sticky === 'topAndLeft' ? '0' : 'auto')};
    left: ${({$sticky}) => ($sticky === 'left' || $sticky === 'topAndLeft' ? '0' : 'auto')};
    z-index: ${({$zIndex}) => $zIndex ?? 'auto'};
    background-color: white;
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
