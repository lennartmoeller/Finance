import {ReactNode} from "react";

interface TableBodyCellProps {
    padding?: string;
    horAlign?: 'left' | 'center' | 'right';
    vertAlign?: 'top' | 'center' | 'bottom';
    colspan?: number;
    headerLevel?: 1 | 2;
    sticky?: 'top' | 'left' | 'topAndLeft';
    width?: number;
    zIndex?: number;
    children: ReactNode;
}

export default TableBodyCellProps;
