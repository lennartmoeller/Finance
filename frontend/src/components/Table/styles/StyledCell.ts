import styled, { type DefaultTheme } from "styled-components";

const getBorderWidth = ({
    theme,
    $headerLevel,
}: {
    theme: DefaultTheme;
    $headerLevel?: 1 | 2;
}) => {
    const borderWidth = theme.border.width;
    const topWidth = $headerLevel ? borderWidth : 0;

    return `${topWidth}px ${borderWidth}px ${borderWidth}px ${borderWidth}px`;
};

const getTopOffset = ({
    theme,
    $headerLevel,
}: {
    theme: DefaultTheme;
    $headerLevel?: 1 | 2;
}) => ($headerLevel ? `-${theme.border.width}px` : "0");

const StyledCell = styled.div<{
    $backgroundColor?: string;
    $headerLevel?: 1 | 2;
}>`
    position: relative;
    background-color: ${(props) => {
        if (props.$backgroundColor) {
            return props.$backgroundColor;
        }
        const cellType: "header1" | "header2" | "body" = props.$headerLevel
            ? `header${props.$headerLevel}`
            : "body";
        return props.theme.table[cellType].backgroundColor;
    }};
    border-style: solid;
    border-color: ${(props) => props.theme.border.color};
    border-width: ${getBorderWidth};

    &::before {
        // border to make sticky cells look good
        position: absolute;
        content: "";
        top: ${getTopOffset};
        right: ${(props) => `${-props.theme.border.width}px`};
        bottom: ${(props) => `${-props.theme.border.width}px`};
        left: ${(props) => `${-props.theme.border.width}px`};
        border-style: solid;
        border-color: ${(props) => props.theme.border.color};
        border-width: ${getBorderWidth};
    }
`;

export default StyledCell;
