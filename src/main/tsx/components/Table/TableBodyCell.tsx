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
    children: ReactNode;
}

export const TableBodyCell: React.FC<TableBodyCellProps> = ({align, colspan = 1, width, children}) => {
    return (
        <StyledBodyCell width={width} align={align} colspan={colspan}>
            {children}
        </StyledBodyCell>
    );
};
