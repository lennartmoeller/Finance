import React, { ReactNode, useRef } from "react";

import { useVirtualizer } from "@tanstack/react-virtual";

import StyledTable from "@/components/Table/styles/StyledTable";
import { memo } from "@/utils/react";

interface TableProps<T> {
    data: Array<T>;
    header: ReactNode;
    body: (element: T, index: number) => ReactNode;
    preRow?: ReactNode;
    postRow?: ReactNode;
    containerHeight?: number;
}

interface TableHeaderProps {
    header: ReactNode;
}

function TableHeaderComponent({ header }: Readonly<TableHeaderProps>) {
    return <thead>{header}</thead>;
}
const TableHeader = memo(TableHeaderComponent, "TableHeader");

function TableComponent<T>({
    data,
    header,
    body,
    preRow,
    postRow,
    containerHeight = 400,
}: Readonly<TableProps<T>>) {
    const parentRef = useRef<HTMLDivElement>(null);

    const virtualizer = useVirtualizer({
        count: data.length,
        getScrollElement: () => parentRef.current,
        estimateSize: () => 50,
    });

    return (
        <div
            ref={parentRef}
            style={{
                height: `${containerHeight}px`,
                overflow: "auto",
            }}
        >
            <StyledTable>
                <TableHeader header={header} />
                <tbody
                    style={{
                        height: `${virtualizer.getTotalSize()}px`,
                        position: "relative",
                    }}
                >
                    {preRow}
                    {virtualizer.getVirtualItems().map((virtualItem) => (
                        <div
                            key={virtualItem.key}
                            data-index={virtualItem.index}
                            ref={virtualizer.measureElement}
                            style={{
                                position: "absolute",
                                top: 0,
                                left: 0,
                                width: "100%",
                                transform: `translateY(${virtualItem.start}px)`,
                                display: "table-row-group",
                            }}
                        >
                            {body(data[virtualItem.index], virtualItem.index)}
                        </div>
                    ))}
                    {postRow}
                </tbody>
            </StyledTable>
        </div>
    );
}
const Table = memo(TableComponent, "Table");

export default Table;
