import React, {ReactNode, useContext} from 'react';

import {LevelContext} from '@/components/Table/LevelContext';

interface TableBodyRowGroupProps {
    children: ReactNode;
}

export const TableBodyRowGroup: React.FC<TableBodyRowGroupProps> = ({children}) => {
    const level: number = useContext(LevelContext);
    return (
        <LevelContext.Provider value={level + 1}>
            {children}
        </LevelContext.Provider>
    );
};