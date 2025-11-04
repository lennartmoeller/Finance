import styled from "styled-components";

const StyledTable = styled.table`
    border-collapse: collapse;
    width: max-content;
    margin: 0;
    padding: 0;
    border-spacing: 0;

    thead {
        position: sticky;
        top: 0;
        z-index: 2;
    }
`;

export default StyledTable;
