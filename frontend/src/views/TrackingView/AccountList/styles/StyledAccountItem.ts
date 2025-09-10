import styled from "styled-components";

interface StyledAccountItemProps {
    $isSelected?: boolean;
}

const StyledAccountItem = styled.div<StyledAccountItemProps>`
    display: grid;
    grid-auto-flow: column;
    grid-auto-columns: max-content;
    place-content: space-between;
    align-content: center;
    align-items: center;
    width: 300px;
    padding: 15px 20px;
    border: ${props => `${props.theme.border.width}px solid ${props.$isSelected ? props.theme.colors.accent : props.theme.border.color}`};
    background-color: ${props => props.$isSelected ? props.theme.colors.accentBackground : 'transparent'};
`;

export default StyledAccountItem;
