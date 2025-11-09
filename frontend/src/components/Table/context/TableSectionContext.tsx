import { createContext, useContext } from "react";

interface TableSectionContextValue {
    isHeader: boolean;
}

const TableSectionContext = createContext<TableSectionContextValue>({ isHeader: false });

export const useTableSection = () => useContext(TableSectionContext);

export default TableSectionContext;
