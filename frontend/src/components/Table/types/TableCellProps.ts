import { ReactNode } from "react";

interface TableCellProps {
    padding?: string;
    horAlign?: "left" | "center" | "right";
    vertAlign?: "top" | "center" | "bottom";
    backgroundColor?: string;
    colspan?: number;
    headerLevel?: 1 | 2;
    children?: ReactNode;
}

export default TableCellProps;
