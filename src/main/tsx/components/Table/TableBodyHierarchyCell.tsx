import React, {useContext} from 'react';

import Button from "@/components/Button/Button";
import ArrowDown from "@/components/Icon/Solid/ArrowDown";
import HierarchyContext from "@/components/Table/context/HierarchyContext";
import StyledTableBodyHierarchyCellContent from "@/components/Table/styles/StyledTableBodyHierarchyCellContent";
import TableBodyCell from "@/components/Table/TableBodyCell";
import TableCellProps from "@/components/Table/types/TableCellProps";

const TableBodyHierarchyCell: React.FC<TableCellProps> = ({children, ...props}) => {
    const hierarchyContext = useContext(HierarchyContext);
    const [hasChildren] = hierarchyContext.hasChildren;
    const [childrenVisible, setChildrenVisible] = hierarchyContext.childrenVisible;

    return (
        <TableBodyCell {...props}>
            <StyledTableBodyHierarchyCellContent $level={hierarchyContext.level}>
                <div>
                    {hasChildren && (
                        <Button onClick={() => setChildrenVisible(!childrenVisible)}>
                            <ArrowDown rotation={childrenVisible ? 0 : -90}/>
                        </Button>
                    )}
                </div>
                {children}
            </StyledTableBodyHierarchyCellContent>
        </TableBodyCell>
    );
};

export default TableBodyHierarchyCell;
