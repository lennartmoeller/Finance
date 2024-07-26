import React, {useContext} from 'react';

import {LevelContext} from '@/components/Table/context/LevelContext';
import {TableBodyCell, TableBodyCellProps} from "@/components/Table/TableBodyCell";

export const TableBodyHierarchyCell: React.FC<TableBodyCellProps> = ({children, ...props}) => {
    const level: number = useContext(LevelContext);
    return <TableBodyCell {...props}>
        {'--'.repeat(level - 1)} {children}
    </TableBodyCell>;
};
