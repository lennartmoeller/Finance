import React, { ReactNode, useCallback, useMemo, useRef } from "react";

import { useVirtualizer } from "@tanstack/react-virtual";
import { useTheme } from "styled-components";

import useForm from "@/components/Form/hooks/useForm";
import Input from "@/components/Form/Input";
import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import StyledTable from "@/components/Table/styles/StyledTable";
import TableCell from "@/components/Table/TableCell";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
import TableCellProps from "@/components/Table/types/TableCellProps";
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
    header?: {
        name: string;
        props?: Omit<TableCellProps, "children">;
    };

    filter?: {
        property: string;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        inputFormatter: InputFormatter<any>;
    };
}

interface TableProps<TFilters extends object = Record<string, unknown>> {
    columns?: TableColumn[];
    stickyHeaderRows?: number;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    rows: Array<TableRowGroup<any>>;
    onFilterChange?: (filters: TFilters) => void;
    initialFilterValues?: Partial<TFilters>;
}

const Table = memo(
    <TFilters extends object = Record<string, unknown>>({
        columns,
        stickyHeaderRows = 0,
        rows = [],
        onFilterChange,
        initialFilterValues = {} as TFilters,
    }: TableProps<TFilters>) => {
        const parentRef = useRef<HTMLDivElement>(null);
        const theme = useTheme();
        const filterBackgroundColor = theme.table.filter.backgroundColor;

        const hasHeaders = useMemo(
            () => columns?.some((column) => column.header !== undefined),
            [columns],
        );

        const hasFilters = useMemo(
            () => columns?.some((column) => column.filter !== undefined),
            [columns],
        );

        // Build initial filter values from columns
        const filterInitialValues = useMemo(
            () =>
                columns?.reduce((acc, column) => {
                    if (column.filter) {
                        const key = column.filter.property as keyof TFilters;
                        acc[key] =
                            (initialFilterValues?.[key] as
                                | TFilters[keyof TFilters]
                                | undefined) ??
                            (null as TFilters[keyof TFilters]);
                    }
                    return acc;
                }, {} as TFilters) ?? ({} as TFilters),
            [columns, initialFilterValues],
        );

        const registerFilter = useForm<TFilters>({
            initial: filterInitialValues,
            onSuccess: async (filters: TFilters) => {
                onFilterChange?.(filters);
            },
        });

        const renderFilterCell = useCallback(
            (column: TableColumn) => {
                if (column.filter) {
                    return (
                        <TableCell
                            key={column.key}
                            as="td"
                            backgroundColor={filterBackgroundColor}
                        >
                            <Input
                                {...registerFilter(
                                    column.filter.property as keyof TFilters,
                                )}
                                inputFormatter={column.filter.inputFormatter}
                            />
                        </TableCell>
                    );
                }
                return (
                    <TableHeaderCell
                        key={column.key}
                        backgroundColor={filterBackgroundColor}
                    />
                );
            },
            [filterBackgroundColor, registerFilter],
        );

        const headerRow = useMemo(
            () =>
                hasHeaders && columns
                    ? {
                          key: "header",
                          content: (
                              <>
                                  {columns.map((column) => (
                                      <TableHeaderCell
                                          key={column.key}
                                          {...column.header?.props}
                                      >
                                          {column.header?.name ?? ""}
                                      </TableHeaderCell>
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
                ...rows.flatMap(
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
            ],
            [headerRow, filterRow, rows],
        );

        const effectiveStickyHeaderRows = hasFilters
            ? stickyHeaderRows + 1
            : stickyHeaderRows;

        const headerRows = useMemo(
            () => allRows.slice(0, effectiveStickyHeaderRows),
            [allRows, effectiveStickyHeaderRows],
        );
        const bodyRows = useMemo(
            () => allRows.slice(effectiveStickyHeaderRows),
            [allRows, effectiveStickyHeaderRows],
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
