import React, { ReactNode, useCallback, useMemo, useRef } from "react";

import { useVirtualizer } from "@tanstack/react-virtual";

import StyledTable from "@/components/Table/styles/StyledTable";

interface TableRowGroupWithData<T> {
    data: Array<T>;
    content: (element: T, index: number) => ReactNode;
    properties?: (
        element: T,
        index: number,
    ) => React.HTMLAttributes<HTMLTableRowElement>;
}

interface TableRowGroupWithoutData {
    content: ReactNode;
    properties?: React.HTMLAttributes<HTMLTableRowElement>;
}

interface TableProps {
    columnWidths?: number[];
    stickyHeaderRows?: number;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    rows: Array<TableRowGroupWithData<any> | TableRowGroupWithoutData>;
}

function isWithData<T>(
    row: TableRowGroupWithData<T> | TableRowGroupWithoutData,
): row is TableRowGroupWithData<T> {
    return (row as TableRowGroupWithData<T>).data !== undefined;
}

const Table = ({
    columnWidths,
    stickyHeaderRows = 0,
    rows = [],
}: TableProps) => {
    const parentRef = useRef<HTMLDivElement>(null);

    const allRows: Array<{
        content: ReactNode;
        properties: React.HTMLAttributes<HTMLTableRowElement>;
    }> = useMemo(() => {
        return rows.flatMap((row) => {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            if (isWithData<any>(row)) {
                return row.data.map((element, index) => ({
                    content: row.content(element, index),
                    properties: row.properties
                        ? row.properties(element, index)
                        : {},
                }));
            }
            return [
                {
                    content: row.content,
                    properties: row.properties ?? {},
                },
            ];
        });
    }, [rows]);

    const headerRows = useMemo(
        () => allRows.slice(0, stickyHeaderRows),
        [allRows, stickyHeaderRows],
    );
    const bodyRows = useMemo(
        () => allRows.slice(stickyHeaderRows),
        [allRows, stickyHeaderRows],
    );

    const estimateSize = useCallback(() => 50, []);

    const virtualizer = useVirtualizer({
        count: bodyRows.length,
        getScrollElement: () => parentRef.current,
        estimateSize,
        overscan: 50,
    });

    const items = virtualizer.getVirtualItems();
    const paddingTop = items.length ? items.at(0)!.start : 0;
    const paddingBottom = items.length
        ? virtualizer.getTotalSize() - items.at(-1)!.end
        : 0;

    const spacerColSpan = columnWidths?.length ?? 9999;

    return (
        <div ref={parentRef} style={{ overflow: "auto", flex: 1 }}>
            <StyledTable>
                {columnWidths && (
                    <colgroup>
                        {columnWidths.map((width, index) => (
                            <col key={index} style={{ width: `${width}px` }} />
                        ))}
                    </colgroup>
                )}

                {headerRows.length > 0 && (
                    <thead>
                        {headerRows.map((rowData, index) => (
                            <tr key={index} {...rowData.properties}>
                                {rowData.content}
                            </tr>
                        ))}
                    </thead>
                )}

                <tbody>
                    {paddingTop > 0 && (
                        <tr aria-hidden="true">
                            <td
                                colSpan={spacerColSpan}
                                style={{ height: paddingTop, padding: 0 }}
                            />
                        </tr>
                    )}

                    {items.map((virtualRow) => {
                        const data = bodyRows[virtualRow.index];
                        return (
                            <tr
                                key={virtualRow.key}
                                data-index={virtualRow.index}
                                ref={virtualizer.measureElement}
                                {...data.properties}
                            >
                                {data.content}
                            </tr>
                        );
                    })}

                    {paddingBottom > 0 && (
                        <tr aria-hidden="true">
                            <td
                                colSpan={spacerColSpan}
                                style={{ height: paddingBottom, padding: 0 }}
                            />
                        </tr>
                    )}
                </tbody>
            </StyledTable>
        </div>
    );
};

export default Table;
