import React, { ReactNode, useCallback, useMemo, useRef } from "react";

import { useVirtualizer } from "@tanstack/react-virtual";
import { useTheme } from "styled-components";

import Input from "@/components/Form/Input";
import TableSectionContext from "@/components/Table/context/TableSectionContext";
import useTableFilters from "@/components/Table/hooks/useTableFilters";
import StyledTable from "@/components/Table/styles/StyledTable";
import TableCell from "@/components/Table/TableCell";
import TableColumn from "@/components/Table/types/TableColumn";
import { memo } from "@/utils/react";

interface TableRowGroup<TData> {
    data?: Array<TData>;
    key: React.Key | ((element: TData, index: number) => React.Key);
    content: ReactNode | ((element: TData, index: number) => ReactNode);
    properties?:
        | React.HTMLAttributes<HTMLTableRowElement>
        | ((element: TData, index: number) => React.HTMLAttributes<HTMLTableRowElement>);
}

interface TableProps<TData = unknown> {
    columns?: Array<TableColumn<TData>>;
    stickyHeaderRows?: number;
    rows: Array<TableRowGroup<TData>>;
}

const Table = memo(<TData = unknown,>({ columns, stickyHeaderRows = 0, rows = [] }: TableProps<TData>) => {
    const parentRef = useRef<HTMLDivElement>(null);
    const theme = useTheme();

    const { hasFilters, registerFilter, filterData } = useTableFilters<TData>({
        columns,
    });

    const hasHeaders = useMemo(() => columns?.some((column) => column.header !== undefined), [columns]);

    const renderFilterCell = useCallback(
        (column: TableColumn<TData>) => {
            if (column.filter) {
                return (
                    <TableCell
                        key={column.key}
                        backgroundColor={theme.table.filter.backgroundColor}
                        fontWeight={theme.table.filter.fontWeight}
                    >
                        <Input
                            {...registerFilter(column.filter.property)}
                            inputFormatter={column.filter.inputFormatter}
                        />
                    </TableCell>
                );
            }
            return (
                <TableCell
                    key={column.key}
                    backgroundColor={theme.table.filter.backgroundColor}
                    fontWeight={theme.table.filter.fontWeight}
                />
            );
        },
        [registerFilter, theme.table.filter.backgroundColor, theme.table.filter.fontWeight],
    );

    const headerRow = useMemo(
        () =>
            hasHeaders && columns
                ? {
                      key: "header",
                      content: (
                          <>
                              {columns.map((column) => (
                                  <TableCell key={column.key} {...column.header?.props}>
                                      {column.header?.name ?? ""}
                                  </TableCell>
                              ))}
                          </>
                      ),
                      properties: {},
                  }
                : null,
        [hasHeaders, columns],
    );

    const filterRow = useMemo(
        () =>
            hasFilters && columns
                ? {
                      key: "filters",
                      content: <>{columns.map(renderFilterCell)}</>,
                      properties: {},
                  }
                : null,
        [hasFilters, columns, renderFilterCell],
    );

    const allRows: Array<{
        key: React.Key;
        content: ReactNode;
        properties: React.HTMLAttributes<HTMLTableRowElement>;
    }> = useMemo(
        () => [
            ...(headerRow ? [headerRow] : []),
            ...(filterRow ? [filterRow] : []),
            ...rows.flatMap((row) => {
                const dataToRender = row.data ? filterData(row.data) : undefined;

                return (
                    dataToRender?.map((element, index) => ({
                        key: typeof row.key === "function" ? row.key(element, index) : row.key,
                        content: typeof row.content === "function" ? row.content(element, index) : row.content,
                        properties:
                            typeof row.properties === "function"
                                ? row.properties(element, index)
                                : (row.properties ?? {}),
                    })) ?? [
                        {
                            key: row.key as React.Key,
                            content: row.content as ReactNode,
                            properties: (row.properties as React.HTMLAttributes<HTMLTableRowElement>) ?? {},
                        },
                    ]
                );
            }),
        ],
        [headerRow, filterRow, rows, filterData],
    );

    const effectiveStickyHeaderRows = hasFilters ? stickyHeaderRows + 1 : stickyHeaderRows;

    const headerRows = useMemo(() => allRows.slice(0, effectiveStickyHeaderRows), [allRows, effectiveStickyHeaderRows]);
    const bodyRows = useMemo(() => allRows.slice(effectiveStickyHeaderRows), [allRows, effectiveStickyHeaderRows]);

    const virtualizer = useVirtualizer({
        count: bodyRows.length,
        getScrollElement: () => parentRef.current,
        estimateSize: () => 50,
        overscan: 50,
    });

    const items = virtualizer.getVirtualItems();
    const paddingTop = items.length ? items.at(0)!.start : 0;
    const paddingBottom = items.length ? virtualizer.getTotalSize() - items.at(-1)!.end : 0;

    const spacerColSpan = columns?.length ?? 9999;

    const headerContextValue = useMemo(() => ({ isHeader: true }), []);
    const bodyContextValue = useMemo(() => ({ isHeader: false }), []);

    return (
        <div ref={parentRef} style={{ overflow: "auto", flex: 1 }}>
            <StyledTable>
                {columns && (
                    <colgroup>
                        {columns.map((column) => (
                            <col key={column.key} style={{ width: `${column.width}px` }} />
                        ))}
                    </colgroup>
                )}

                {headerRows.length > 0 && (
                    <thead>
                        <TableSectionContext.Provider value={headerContextValue}>
                            {headerRows.map((rowData) => (
                                <tr key={rowData.key} {...rowData.properties}>
                                    {rowData.content}
                                </tr>
                            ))}
                        </TableSectionContext.Provider>
                    </thead>
                )}

                <tbody>
                    <TableSectionContext.Provider value={bodyContextValue}>
                        {paddingTop > 0 && (
                            <tr aria-hidden="true">
                                <td colSpan={spacerColSpan} style={{ height: paddingTop, padding: 0 }} />
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
                    </TableSectionContext.Provider>
                </tbody>
            </StyledTable>
        </div>
    );
}, "Table");

export default Table;
