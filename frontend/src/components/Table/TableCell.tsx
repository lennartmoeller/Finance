import React, { ReactNode } from "react";

import styled from "styled-components";

import { useTableSection } from "@/components/Table/context/TableSectionContext";
import StyledCell from "@/components/Table/styles/StyledCell";
import StyledCellContent from "@/components/Table/styles/StyledCellContent";
import { memo } from "@/utils/react";

interface TableCellProps {
    padding?: string;
    horAlign?: "left" | "center" | "right";
    vertAlign?: "top" | "center" | "bottom";
    backgroundColor?: string;
    fontWeight?: string;
    colspan?: number;
    headerLevel?: 1 | 2;
    children?: ReactNode;
}

const StyledTableCell = styled(StyledCell)<{ as: "td" | "th" }>``;

const TableCell: React.FC<TableCellProps> = memo(
    ({ headerLevel, padding, horAlign, vertAlign, backgroundColor, fontWeight, colspan, children }) => {
        const { isHeader } = useTableSection();
        const as = isHeader ? "th" : "td";
        const effectiveHeaderLevel = as === "th" ? (headerLevel ?? 1) : undefined;

        return (
            <StyledTableCell
                as={as}
                colSpan={colspan}
                $backgroundColor={backgroundColor}
                $headerLevel={effectiveHeaderLevel}
            >
                <StyledCellContent
                    $headerLevel={effectiveHeaderLevel}
                    $padding={padding}
                    $horAlign={horAlign}
                    $vertAlign={vertAlign}
                    $fontWeight={fontWeight}
                >
                    {children}
                </StyledCellContent>
            </StyledTableCell>
        );
    },
    "TableCell",
);

export default TableCell;
