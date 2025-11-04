import React from "react";

import styled from "styled-components";

import StyledCell from "@/components/TableOld/styles/StyledCell";
import StyledCellContent from "@/components/TableOld/styles/StyledCellContent";
import TableCellProps from "@/components/TableOld/types/TableCellProps";

const StyledHeaderCell = styled(StyledCell).attrs({ as: "th" })``;

const TableHeaderCell: React.FC<TableCellProps> = ({
    headerLevel = 1,
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
        <StyledHeaderCell
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
        </StyledHeaderCell>
    );
};

export default TableHeaderCell;
