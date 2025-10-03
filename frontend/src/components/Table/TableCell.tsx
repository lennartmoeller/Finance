import React from "react";

import styled from "styled-components";

import StyledCell from "@/components/Table/styles/StyledCell";
import StyledCellContent from "@/components/Table/styles/StyledCellContent";
import TableCellProps from "@/components/Table/types/TableCellProps";
import { memo } from "@/utils/react";

interface TableCellInternalProps extends TableCellProps {
    as: "td" | "th";
}

const StyledTableCell = styled(StyledCell)<{ as: "td" | "th" }>``;

const TableCell: React.FC<TableCellInternalProps> = memo(
    ({
        as,
        headerLevel,
        padding,
        horAlign,
        vertAlign,
        backgroundColor,
        colspan,
        children,
    }) => {
        return (
            <StyledTableCell
                as={as}
                colSpan={colspan}
                $backgroundColor={backgroundColor}
                $headerLevel={headerLevel}
            >
                <StyledCellContent
                    $headerLevel={headerLevel}
                    $padding={padding}
                    $horAlign={horAlign}
                    $vertAlign={vertAlign}
                >
                    {children}
                </StyledCellContent>
            </StyledTableCell>
        );
    },
    "TableCell",
);

export default TableCell;
