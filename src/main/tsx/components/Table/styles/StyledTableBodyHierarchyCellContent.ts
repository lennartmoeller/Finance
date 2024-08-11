import styled from "styled-components";

const StyledTableBodyHierarchyCellContent = styled.div<{
    $level: number,
}>`
    display: grid;
    grid-template-columns: 16px 1fr;
    align-items: center;
    margin-left: ${({$level}) => `${($level - 1) * 10}px`};
`;

export default StyledTableBodyHierarchyCellContent;
