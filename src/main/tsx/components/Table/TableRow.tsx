import React, {ReactNode} from 'react';

interface TableBodyRowProps {
    children: ReactNode;
}

const TableRow: React.FC<TableBodyRowProps> = ({children}) => {
    return (
        <tr>
            {children}
        </tr>
    );
};

export default TableRow;
