import React, {ReactNode} from 'react';

interface TableBodyRowProps {
    id: string;
    parentRowId?: string;
    children: ReactNode;
}

export const TableBodyRow: React.FC<TableBodyRowProps> = ({id, parentRowId, children}) => {
    return (
        <tr id={id}>{children}</tr>
    );
};
