import React, {ReactNode} from "react";

import styled from "styled-components";

const StyledTable = styled.table`
    border-collapse: collapse;
    width: max-content;
`;

export const StyledCell = styled.div<{ align?: 'left' | 'center' | 'right', width?: string }>`
    width: ${({width}) => width || 'auto'};
    text-align: ${({align}) => align || 'left'};
    border: 1px solid #e3e3e3;
`;

interface TableProps<T> {
    data: Array<T>;
    header: ReactNode;
    body: (element: T, index: number) => ReactNode;
}

export const Table = <T, >({data, header, body}: TableProps<T>) => {
    return (
        <StyledTable>
            <thead>
            {header}
            </thead>
            <tbody>
            {data.map((element: T, index: number) => body(element, index))}
            </tbody>
        </StyledTable>
    );
};
