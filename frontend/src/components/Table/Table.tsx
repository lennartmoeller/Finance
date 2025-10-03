import React, { ReactNode, useCallback, useMemo, useRef } from "react";

import { useVirtualizer } from "@tanstack/react-virtual";

import StyledTable from "@/components/Table/styles/StyledTable";
import { memo } from "@/utils/react";

interface TableRowGroup<T> {
    data?: Array<T>;
    key: React.Key | ((element: T, index: number) => React.Key);
    content: ReactNode | ((element: T, index: number) => ReactNode);
    properties?:
        | React.HTMLAttributes<HTMLTableRowElement>
        | ((
              element: T,
              index: number,
          ) => React.HTMLAttributes<HTMLTableRowElement>);
}

interface TableColumn {
    key: React.Key;
    width: number;
}

interface TableProps {
    columns?: TableColumn[];
    stickyHeaderRows?: number;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    rows: Array<TableRowGroup<any>>;
}

const Table = memo(
    ({ columns, stickyHeaderRows = 0, rows = [] }: TableProps) => {
        const parentRef = useRef<HTMLDivElement>(null);

        const allRows: Array<{
            key: React.Key;
            content: ReactNode;
            properties: React.HTMLAttributes<HTMLTableRowElement>;
        }> = useMemo(
            () =>
                rows.flatMap(
                    (row) =>
                        row.data?.map((element, index) => ({
                            key:
                                typeof row.key === "function"
                                    ? row.key(element, index)
                                    : row.key,
                            content:
                                typeof row.content === "function"
                                    ? row.content(element, index)
                                    : row.content,
                            properties:
                                typeof row.properties === "function"
                                    ? row.properties(element, index)
                                    : (row.properties ?? {}),
                        })) ?? [
                            {
                                key: row.key as React.Key,
                                content: row.content as ReactNode,
                                properties:
                                    (row.properties as React.HTMLAttributes<HTMLTableRowElement>) ??
                                    {},
                            },
                        ],
                ),
            [rows],
        );

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

        const spacerColSpan = columns?.length ?? 9999;

        return (
            <div ref={parentRef} style={{ overflow: "auto", flex: 1 }}>
                <StyledTable>
                    {columns && (
                        <colgroup>
                            {columns.map((column) => (
                                <col
                                    key={column.key}
                                    style={{ width: `${column.width}px` }}
                                />
                            ))}
                        </colgroup>
                    )}

                    {headerRows.length > 0 && (
                        <thead>
                            {headerRows.map((rowData) => (
                                <tr key={rowData.key} {...rowData.properties}>
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
                                    key={data.key}
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
                                    style={{
                                        height: paddingBottom,
                                        padding: 0,
                                    }}
                                />
                            </tr>
                        )}
                    </tbody>
                </StyledTable>
            </div>
        );
    },
    "Table",
);

export default Table;
