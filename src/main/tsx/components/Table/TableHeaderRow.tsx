import React, {ReactNode} from 'react';

interface TableHeaderRowProps {
    children: ReactNode;
}

export const TableHeaderRow: React.FC<TableHeaderRowProps> = ({children}) => {
    return <tr>
        {children}
    </tr>;
};
