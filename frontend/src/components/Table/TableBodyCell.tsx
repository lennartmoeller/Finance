import React from "react";

import TableCell from "@/components/Table/TableCell";
import TableCellProps from "@/components/Table/types/TableCellProps";

const TableBodyCell: React.FC<TableCellProps> = (props) => {
    return <TableCell as="td" {...props} />;
};

export default TableBodyCell;
