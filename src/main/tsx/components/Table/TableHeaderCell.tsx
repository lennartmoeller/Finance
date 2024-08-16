import React from 'react';

import styled from "styled-components";

import StyledCell from "@/components/Table/styles/StyledCell";
import StyledCellContent from "@/components/Table/styles/StyledCellContent";
import TableCellProps from "@/components/Table/types/TableCellProps";

const StyledHeaderCell = styled(StyledCell).attrs({as: 'th'})``;

const TableHeaderCell: React.FC<TableCellProps> = (
    {
        headerLevel = 1,
        padding,
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
            $headerLevel={headerLevel}
            $sticky={sticky}
            $width={width}
            $zIndex={zIndex}>
            <StyledCellContent
                $headerLevel={headerLevel}
                $padding={padding}
                $horAlign={horAlign}
                $vertAlign={vertAlign}>
                {children}
            </StyledCellContent>
        </StyledHeaderCell>
    );
};

export default TableHeaderCell;
