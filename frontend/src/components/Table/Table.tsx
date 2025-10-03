import React, { ReactNode, useRef } from "react";

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

const Table = ({
    columnWidths,
    stickyHeaderRows = 0,
    rows = [],
}: TableProps) => {
    const parentRef = useRef<HTMLDivElement>(null);

    const allRows: Array<{
        content: ReactNode;
        properties: React.HTMLAttributes<HTMLTableRowElement>;
    }> = rows.flatMap((row) => {
        if (Object.hasOwn(row, "data")) {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            const row2 = row as TableRowGroupWithData<any>;
            return row2.data.map((element, index) => ({
                content: row2.content(element, index),
                properties: row2.properties
                    ? row2.properties(element, index)
                    : {},
            }));
        } else {
            const row2 = row as TableRowGroupWithoutData;
            return {
                content: row2.content,
                properties: row2.properties ?? {},
            };
        }
    });

    const headerRows = allRows.slice(0, stickyHeaderRows);
    const bodyRows = allRows.slice(stickyHeaderRows);

    const virtualizer = useVirtualizer({
        count: bodyRows.length,
        getScrollElement: () => parentRef.current,
        estimateSize: () => 50,
        overscan: 50,
    });

    const items = virtualizer.getVirtualItems();
    const paddingTop = items.length ? items.at(0)!.start : 0;
    const paddingBottom = items.length
        ? virtualizer.getTotalSize() - items.at(-1)!.end
        : 0;

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
                <thead>
                    {headerRows.map((rowData, index) => (
                        <tr key={index} {...rowData.properties}>
                            {rowData.content}
                        </tr>
                    ))}
                </thead>
                <tbody>
                    {paddingTop > 0 && (
                        <tr aria-hidden="true">
                            <td
                                colSpan={9999}
                                style={{ height: paddingTop, padding: 0 }}
                            />
                        </tr>
                    )}

                    {virtualizer.getVirtualItems().map((virtualRow) => {
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
                                colSpan={9999}
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
