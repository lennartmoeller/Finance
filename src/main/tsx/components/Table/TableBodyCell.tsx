import React from 'react';

import styled from "styled-components";

import StyledCell from "@/components/Table/styles/StyledCell";
import StyledCellContent from "@/components/Table/styles/StyledCellContent";
import TableCellProps from "@/components/Table/types/TableCellProps";

const StyledBodyCell = styled(StyledCell).attrs({as: 'td'})``;

const TableBodyCell: React.FC<TableCellProps> = (
    {
        headerLevel,
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
        <StyledBodyCell
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
        </StyledBodyCell>
    );
};

export default TableBodyCell;
