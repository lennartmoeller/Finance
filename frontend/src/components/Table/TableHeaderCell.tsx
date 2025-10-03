import React from "react";

import TableCell from "@/components/Table/TableCell";
import TableCellProps from "@/components/Table/types/TableCellProps";

const TableHeaderCell: React.FC<TableCellProps> = ({
    headerLevel = 1,
    ...props
}) => {
    return <TableCell as="th" headerLevel={headerLevel} {...props} />;
};

export default TableHeaderCell;
