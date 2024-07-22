import React, {ReactNode} from 'react';

import {StyledHeaderCell} from "@/components/Table/style";

interface TableHeaderCellProps {
    children: ReactNode;
    width?: string;
}

export const TableHeaderCell: React.FC<TableHeaderCellProps> = ({children, width}) => {
    return (
        <StyledHeaderCell width={width}>{children}</StyledHeaderCell>
    );
};
