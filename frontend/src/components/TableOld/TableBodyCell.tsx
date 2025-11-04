import React from "react";

import styled from "styled-components";

import StyledCell from "@/components/TableOld/styles/StyledCell";
import StyledCellContent from "@/components/TableOld/styles/StyledCellContent";
import TableCellProps from "@/components/TableOld/types/TableCellProps";

const StyledBodyCell = styled(StyledCell).attrs({ as: "td" })``;

const TableBodyCell: React.FC<TableCellProps> = ({
    headerLevel,
    padding,
    horAlign,
    vertAlign,
    backgroundColor,
    colspan,
    sticky,
    width,
    zIndex,
    children,
}) => {
    return (
        <StyledBodyCell
            colSpan={colspan}
            $backgroundColor={backgroundColor}
            $headerLevel={headerLevel}
            $sticky={sticky}
            $width={width}
            $zIndex={zIndex}
        >
            <StyledCellContent
                $headerLevel={headerLevel}
                $padding={padding}
                $horAlign={horAlign}
                $vertAlign={vertAlign}
            >
                {children}
            </StyledCellContent>
        </StyledBodyCell>
    );
};

export default TableBodyCell;
