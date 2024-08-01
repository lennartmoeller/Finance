import React, {ReactNode} from 'react';

import StyledBodyCell from "@/components/Table/styles/StyledBodyCell";
import StyledCellContent from "@/components/Table/styles/StyledCellContent";

export interface TableBodyCellProps {
    horAlign?: 'left' | 'center' | 'right';
    vertAlign?: 'top' | 'center' | 'bottom';
    colspan?: number;
    sticky?: 'top' | 'left' | 'topAndLeft';
    width?: number;
    zIndex?: number;
    children: ReactNode;
}

const TableBodyCell: React.FC<TableBodyCellProps> = (
    {
        horAlign,
        vertAlign,
        colspan,
        sticky,
        width,
        zIndex,
        children,
    }
) => {
    return (
        <StyledBodyCell
            colSpan={colspan}
            $sticky={sticky}
            $width={width}
            $zIndex={zIndex}>
            <StyledCellContent
                $horAlign={horAlign}
                $vertAlign={vertAlign}>
                {children}
            </StyledCellContent>
        </StyledBodyCell>
    );
};

export default TableBodyCell;
