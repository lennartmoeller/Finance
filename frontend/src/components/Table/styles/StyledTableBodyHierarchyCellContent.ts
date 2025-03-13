import styled from "styled-components";

const StyledTableBodyHierarchyCellContent = styled.div<{
    $level: number,
}>`
    display: grid;
    grid-auto-flow: column;
    gap: 6px;
    align-items: center;
    margin-left: ${({$level}) => `${($level - 1) * 10}px`};
`;

export default StyledTableBodyHierarchyCellContent;
