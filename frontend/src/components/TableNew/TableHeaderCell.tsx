import React from "react";

import TableCell from "@/components/TableNew/TableCell";
import TableCellProps from "@/components/TableNew/types/TableCellProps";
import { memo } from "@/utils/react";

const TableHeaderCell: React.FC<TableCellProps> = memo(
    ({ headerLevel = 1, ...props }) => {
        return <TableCell as="th" headerLevel={headerLevel} {...props} />;
    },
    "TableHeaderCell",
);

export default TableHeaderCell;
