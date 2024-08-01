import React, {ReactNode} from 'react';

import StyledCellContent from "@/components/Table/styles/StyledCellContent";
import StyledHeaderCell from "@/components/Table/styles/StyledHeaderCell";

interface TableHeaderCellProps {
    horAlign?: 'left' | 'center' | 'right';
    vertAlign?: 'top' | 'center' | 'bottom';
    colspan?: number;
    sticky?: 'top' | 'left' | 'topAndLeft';
    width?: number;
    zIndex?: number;
    children: ReactNode;
}

const TableHeaderCell: React.FC<TableHeaderCellProps> = (
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
        <StyledHeaderCell
            colSpan={colspan}
            $sticky={sticky}
            $width={width}
            $zIndex={zIndex}>
            <StyledCellContent
                $horAlign={horAlign}
                $vertAlign={vertAlign}>
                {children}
            </StyledCellContent>
        </StyledHeaderCell>
    );
};

export default TableHeaderCell;
