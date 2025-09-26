import styled from "styled-components";

const StyledDiagramGrid = styled.div`
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));

    gap: ${({ theme }) => `${theme.mainPadding}px`};
    padding: ${({ theme }) => `${theme.mainPadding}px`};
`;

export default StyledDiagramGrid;
