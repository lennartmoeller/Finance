import React, {ReactNode, useContext} from 'react';

import LevelContext from '@/components/Table/context/LevelContext';

interface TableBodyRowGroupProps {
    children: ReactNode;
}

const TableHierarchyLevel: React.FC<TableBodyRowGroupProps> = ({children}) => {
    const level: number = useContext(LevelContext);
    return (
        <LevelContext.Provider value={level + 1}>
            {children}
        </LevelContext.Provider>
    );
};

export default TableHierarchyLevel;
