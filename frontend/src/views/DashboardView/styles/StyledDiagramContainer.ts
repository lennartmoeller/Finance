import styled from "styled-components";

const StyledDiagramContainer = styled.div`
    border: ${({ theme }) =>
        `${theme.border.width}px solid ${theme.border.color}`};
    border-radius: ${({ theme }) => `${theme.border.radius}px`};
    overflow: hidden;
`;

export default StyledDiagramContainer;
