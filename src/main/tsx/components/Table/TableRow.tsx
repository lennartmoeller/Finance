import React, {ReactNode} from 'react';

interface TableBodyRowProps {
    children: ReactNode;
}

export const TableRow: React.FC<TableBodyRowProps> = ({children}) => {
    return (
        <tr>
            {children}
        </tr>
    );
};
