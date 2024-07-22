import styled from "styled-components";

const StyledCell = styled.div`
    border: 1px solid #e3e3e3;
`;

export const StyledTable = styled.table`
    border-collapse: collapse;
`;

export const StyledHeaderCell = styled(StyledCell).attrs({as: 'th'})<{ width?: string }>`
    width: ${({width}) => width || 'auto'};
    margin: 0;
    padding: 0;
    white-space: nowrap;
    background-color: #f8f8f8;
`;

export const StyledBodyCell = styled(StyledCell).attrs({as: 'td'})``;
