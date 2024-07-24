import React, {ReactNode, useContext} from 'react';

import {LevelContext} from '@/components/Table/LevelContext';
import {TableBodyCell} from "@/components/Table/TableBodyCell";

interface TableBodyHierarchyCellProps {
    children: ReactNode;
}

export const TableBodyHierarchyCell: React.FC<TableBodyHierarchyCellProps> = ({children}) => {
    const level: number = useContext(LevelContext);
    return <TableBodyCell>
        {'--'.repeat(level - 1)} {children}
    </TableBodyCell>;
};
