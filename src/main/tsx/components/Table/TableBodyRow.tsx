import React, {ReactNode} from 'react';

interface TableBodyRowProps {
    children: ReactNode;
}

export const TableBodyRow: React.FC<TableBodyRowProps> = ({children}) => {
    return <tr>
        {children}
    </tr>;
};
