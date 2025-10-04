import styled from "styled-components";

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
    border: ${(props) =>
        `${props.theme.border.width}px solid ${props.theme.border.color}`};

    &::before {
        // border to make sticky cells look good
        position: absolute;
        content: "";
        top: ${(props) => `${-props.theme.border.width}px`};
        right: ${(props) => `${-props.theme.border.width}px`};
        bottom: ${(props) => `${-props.theme.border.width}px`};
        left: ${(props) => `${-props.theme.border.width}px`};
        border: ${(props) =>
            `${props.theme.border.width}px solid ${props.theme.border.color}`};
    }
`;

export default StyledCell;
