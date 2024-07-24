import React, {ReactNode} from 'react';

import styled from "styled-components";

import {StyledCell} from "@/components/Table/Table";

const StyledBodyCell = styled(StyledCell).attrs({as: 'td'})<{ colspan?: number; }>`
    column-span: ${({colspan}) => colspan || 1};
`;

interface TableBodyCellProps {
    align?: 'left' | 'center' | 'right';
    colspan?: number;
    width?: string;
    sticky?: 'top' | 'left' | 'topAndLeft';
    zIndex?: number;
    children: ReactNode;
}

export const TableBodyCell: React.FC<TableBodyCellProps> = ({align, colspan = 1, width, sticky, zIndex, children}) => {
    return <StyledBodyCell
        width={width}
        align={align}
        colspan={colspan}
        sticky={sticky}
        zIndex={zIndex}>
        {children}
    </StyledBodyCell>;
};
