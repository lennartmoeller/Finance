import React, {ReactNode} from "react";

import styled from "styled-components";

const StyledTable = styled.table`
    border-collapse: collapse;
    width: max-content;
`;

export const StyledCell = styled.div<{
    align?: 'left' | 'center' | 'right',
    width?: string,
    sticky?: 'top' | 'left' | 'topAndLeft',
    zIndex?: number
}>`
    width: ${({width}) => width || 'auto'};
    text-align: ${({align}) => align || 'left'};
    position: ${({sticky}) => (sticky ? 'sticky' : 'static')};
    top: ${({sticky}) => (sticky === 'top' || sticky === 'topAndLeft' ? '0' : 'auto')};
    left: ${({sticky}) => (sticky === 'left' || sticky === 'topAndLeft' ? '0' : 'auto')};
    z-index: ${({zIndex}) => zIndex || 'auto'};
    background-color: ${({sticky}) => (sticky ? '#fff' : 'inherit')};
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
