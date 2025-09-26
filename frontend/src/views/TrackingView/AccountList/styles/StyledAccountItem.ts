import styled, { DefaultTheme } from "styled-components";

interface StyledAccountItemProps {
    $isSelected?: boolean;
}

interface ThemeProps {
    theme: DefaultTheme;
    $isSelected?: boolean;
}

const getSelectionBorderColor = (props: ThemeProps) =>
    props.$isSelected ? props.theme.colors.accent : props.theme.border.color;

const getSelectionBackground = (props: ThemeProps) =>
    props.$isSelected ? props.theme.colors.accentBackground : "transparent";

const StyledAccountItem = styled.div<StyledAccountItemProps>`
    display: grid;
    grid-auto-flow: column;
    grid-auto-columns: max-content;
    place-content: space-between;
    align-content: center;
    align-items: center;
    width: 300px;
    padding: 15px 20px;
    border: ${(props) =>
        `${props.theme.border.width}px solid ${getSelectionBorderColor(props)}`};
    background-color: ${getSelectionBackground};
`;

export default StyledAccountItem;
