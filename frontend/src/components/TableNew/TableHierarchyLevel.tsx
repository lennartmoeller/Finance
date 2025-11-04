import React, { ReactNode, useContext, useState } from "react";

import HierarchyContext from "@/components/TableNew/context/HierarchyContext";

interface TableBodyRowGroupProps {
    initiallyOpen?: boolean;
    children: ReactNode;
}

const TableHierarchyLevel: React.FC<TableBodyRowGroupProps> = ({
    initiallyOpen,
    children,
}) => {
    const childrenVisibleState = useState<boolean>(initiallyOpen ?? true);
    const hasChildrenState = useState<boolean>(false);

    const parentsHierarchyContext = useContext(HierarchyContext);

    const [, setHasParentChildren] = parentsHierarchyContext.hasChildren;
    setHasParentChildren(true);

    const [parentsChildrenVisible] = parentsHierarchyContext.childrenVisible;
    if (!parentsChildrenVisible) return <></>;

    return (
        <HierarchyContext.Provider
            value={{
                level: parentsHierarchyContext.level + 1,
                childrenVisible: childrenVisibleState,
                hasChildren: hasChildrenState,
            }}
        >
            {children}
        </HierarchyContext.Provider>
    );
};

export default TableHierarchyLevel;
