import React, {ReactNode} from 'react';

import styled from "styled-components";

import {StyledCell} from "@/components/Table/Table";

const StyledHeaderCell = styled(StyledCell).attrs({as: 'th'})`
    margin: 0;
    padding: 0;
    white-space: nowrap;
    background-color: #f8f8f8;
`;

interface TableHeaderCellProps {
    width?: string;
    align?: 'left' | 'center' | 'right';
    sticky?: 'top' | 'left' | 'topAndLeft';
    zIndex?: number;
    children: ReactNode;
}

export const TableHeaderCell: React.FC<TableHeaderCellProps> = ({width, align, sticky, zIndex, children}) => {
    return <StyledHeaderCell
        width={width}
        align={align}
        sticky={sticky}
        zIndex={zIndex}>
        {children}
    </StyledHeaderCell>;
};
