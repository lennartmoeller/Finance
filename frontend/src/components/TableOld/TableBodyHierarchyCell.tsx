import React, { useContext } from "react";

import Button from "@/components/Button/Button";
import Icon from "@/components/Icon/Icon";
import HierarchyContext from "@/components/TableOld/context/HierarchyContext";
import StyledTableBodyHierarchyCellContent from "@/components/TableOld/styles/StyledTableBodyHierarchyCellContent";
import TableBodyCell from "@/components/TableOld/TableBodyCell";
import TableCellProps from "@/components/TableOld/types/TableCellProps";

const TableBodyHierarchyCell: React.FC<TableCellProps> = ({ children, ...props }) => {
    const hierarchyContext = useContext(HierarchyContext);
    const [hasChildren] = hierarchyContext.hasChildren;
    const [childrenVisible, setChildrenVisible] = hierarchyContext.childrenVisible;

    const contents = (
        <StyledTableBodyHierarchyCellContent $level={hierarchyContext.level}>
            <Icon id="fa-solid fa-caret-down" rotation={childrenVisible ? 0 : -90} opacity={hasChildren ? 1 : 0} />
            {children}
        </StyledTableBodyHierarchyCellContent>
    );

    if (!hasChildren) return <TableBodyCell {...props}>{contents}</TableBodyCell>;

    return (
        <TableBodyCell {...props}>
            <Button onClick={() => setChildrenVisible(!childrenVisible)}>{contents}</Button>
        </TableBodyCell>
    );
};

export default TableBodyHierarchyCell;
