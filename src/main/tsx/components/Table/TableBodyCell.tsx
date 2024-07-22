import React, {ReactNode} from 'react';

import {StyledBodyCell} from "@/components/Table/style";

interface TableBodyCellProps {
    children: ReactNode;
}

export const TableBodyCell: React.FC<TableBodyCellProps> = ({children}) => {
    return (
        <StyledBodyCell>{children}</StyledBodyCell>
    );
};
