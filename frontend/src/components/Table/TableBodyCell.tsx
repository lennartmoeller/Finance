import React from "react";

import TableCell from "@/components/Table/TableCell";
import TableCellProps from "@/components/Table/types/TableCellProps";
import { memo } from "@/utils/react";

const TableBodyCell: React.FC<TableCellProps> = memo((props) => {
    return <TableCell as="td" {...props} />;
}, "TableBodyCell");

export default TableBodyCell;
