import React, { ReactNode } from "react";

interface TableBodyRowProps {
    children: ReactNode;
    onFocus?: () => void;
    onBlur?: () => void;
}

const TableRow: React.FC<TableBodyRowProps> = ({ children, onFocus, onBlur }) => {
    return <tr onFocus={onFocus} onBlur={onBlur}>{children}</tr>;
};

export default TableRow;
