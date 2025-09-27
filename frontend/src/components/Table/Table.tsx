import React, {
    type CSSProperties,
    type HTMLAttributes,
    type MutableRefObject,
    type ReactNode,
    type Ref,
    type RefCallback,
    useCallback,
    useEffect,
    useMemo,
    useRef,
    useState,
} from "react";

import { useVirtualizer } from "@tanstack/react-virtual";

import StyledTable from "@/components/Table/styles/StyledTable";
import { memo } from "@/utils/react";

const areArraysEqual = (first: number[], second: number[]) =>
    first.length === second.length &&
    first.every((value, index) => value === second[index]);

const mergeRefs = <Value,>(
    ...refs: Array<Ref<Value> | null | undefined>
): RefCallback<Value> => {
    return (instance) => {
        refs.forEach((ref) => {
            if (!ref) {
                return;
            }

            if (typeof ref === "function") {
                ref(instance);
                return;
            }

            (ref as MutableRefObject<Value | null>).current = instance;
        });
    };
};

const forwardRefSymbol = Symbol.for("react.forward_ref");

const isForwardRefComponent = (type: unknown): boolean =>
    typeof type === "object" &&
    type !== null &&
    "$$typeof" in (type as Record<string, unknown>) &&
    (type as { $$typeof?: symbol }).$$typeof === forwardRefSymbol;

const isVirtualizableRow = (node: ReactNode): boolean => {
    if (!React.isValidElement(node)) {
        return false;
    }

    if (node.type === React.Fragment) {
        const { children } = node.props as { children?: ReactNode };
        return React.Children.toArray(children).some(isVirtualizableRow);
    }

    if (typeof node.type === "string") {
        return node.type === "tr";
    }

    return isForwardRefComponent(node.type);
};

interface TableProps<T> {
    data: Array<T>;
    header: ReactNode;
    body: (element: T, index: number) => ReactNode;
    preRow?: ReactNode;
    postRow?: ReactNode;
}

function TableComponent<T>({
    data,
    header,
    body,
    preRow,
    postRow,
}: Readonly<TableProps<T>>) {
    const containerRef = useRef<HTMLDivElement>(null);
    const scrollElementRef = useRef<HTMLDivElement>(null);
    const measurementTableRef = useRef<HTMLTableElement>(null);
    const headerRef = useRef<HTMLTableSectionElement>(null);
    const [containerHeight, setContainerHeight] = useState<number>(400);
    const [tableWidth, setTableWidth] = useState<number | undefined>(undefined);
    const [columnWidths, setColumnWidths] = useState<number[]>([]);

    const sampleRow = useMemo(
        () => (data.length > 0 ? body(data[0], 0) : null),
        [body, data],
    );

    const virtualizationCompatible = useMemo(() => {
        if (preRow && !isVirtualizableRow(preRow)) {
            return false;
        }

        if (postRow && !isVirtualizableRow(postRow)) {
            return false;
        }

        if (sampleRow && !isVirtualizableRow(sampleRow)) {
            return false;
        }

        return true;
    }, [postRow, preRow, sampleRow]);

    const renderRowElement = (
        row: ReactNode,
        options: Omit<HTMLAttributes<HTMLTableRowElement>, "ref"> & {
            key?: React.Key;
            ref?: Ref<HTMLTableRowElement>;
            style?: CSSProperties;
            "data-index"?: string;
        },
    ) => {
        if (row === null || row === undefined) {
            return null;
        }

        const { key, ref, style, className, ...rest } = options;

        if (React.isValidElement(row) && isVirtualizableRow(row)) {
            const element = row as React.ReactElement<
                HTMLAttributes<HTMLTableRowElement>
            > & { ref?: Ref<HTMLTableRowElement> };

            const existingStyle = (element.props.style ?? {}) as CSSProperties;
            const mergedStyle = {
                ...existingStyle,
                ...style,
            } satisfies CSSProperties;

            const existingClassName = element.props.className ?? "";
            const mergedClassName = [existingClassName, className]
                .filter(Boolean)
                .join(" ");

            const mergedRef = mergeRefs<HTMLTableRowElement>(element.ref, ref);

            return React.cloneElement(element, {
                ...rest,
                className: mergedClassName || undefined,
                style: mergedStyle,
                ref: mergedRef,
                key,
            } as Record<string, unknown>);
        }

        return (
            <tr
                key={key}
                ref={ref}
                className={className}
                style={style}
                {...rest}
            >
                {row}
            </tr>
        );
    };

    // Calculate dynamic container height based on available space
    const updateContainerHeight = useCallback(() => {
        if (!containerRef.current?.parentElement) {
            setContainerHeight(400);
            return;
        }

        const parentElement = containerRef.current.parentElement;
        const parentRect = parentElement.getBoundingClientRect();

        let availableHeight = parentRect.height;

        Array.from(parentElement.children).forEach((child) => {
            if (child !== containerRef.current) {
                availableHeight -= child.getBoundingClientRect().height;
            }
        });

        const newHeight = Math.max(
            200,
            Math.min(availableHeight - 20, window.innerHeight * 0.8),
        );
        setContainerHeight(newHeight);
    }, []);

    const updateTableMeasurements = useCallback(() => {
        if (!measurementTableRef.current) {
            setTableWidth(undefined);
            setColumnWidths([]);
            return;
        }

        const width = measurementTableRef.current.getBoundingClientRect().width;
        setTableWidth(width > 0 ? width : undefined);

        const firstBodyRow =
            measurementTableRef.current.querySelector<HTMLTableRowElement>(
                "tbody tr",
            );

        if (!firstBodyRow) {
            setColumnWidths([]);
            return;
        }

        const widths: number[] = [];
        Array.from(firstBodyRow.cells).forEach((cell: HTMLTableCellElement) => {
            const cellWidth = cell.getBoundingClientRect().width;
            const span = cell.colSpan || 1;
            const widthPerColumn = span > 0 ? cellWidth / span : 0;
            for (let index = 0; index < span; index += 1) {
                widths.push(widthPerColumn);
            }
        });

        setColumnWidths((previous) =>
            areArraysEqual(previous, widths) ? previous : widths,
        );
    }, []);

    useEffect(() => {
        if (!virtualizationCompatible) {
            return;
        }

        updateContainerHeight();
        updateTableMeasurements();

        const handleResize = () => {
            updateContainerHeight();
            updateTableMeasurements();
        };

        window.addEventListener("resize", handleResize);

        const timeoutId = setTimeout(() => {
            updateContainerHeight();
            updateTableMeasurements();
        }, 100);

        return () => {
            window.removeEventListener("resize", handleResize);
            clearTimeout(timeoutId);
        };
    }, [
        data,
        updateContainerHeight,
        updateTableMeasurements,
        virtualizationCompatible,
    ]);

    useEffect(() => {
        if (!virtualizationCompatible) {
            return;
        }

        if (!headerRef.current) {
            return;
        }

        const headerRow = headerRef.current.querySelector("tr");

        if (!headerRow) {
            return;
        }

        if (columnWidths.length === 0) {
            Array.from(headerRow.cells).forEach((cell) => {
                (cell as HTMLTableCellElement).style.width = "";
            });
            return;
        }

        let columnIndex = 0;
        Array.from(headerRow.cells).forEach((cell) => {
            const span = cell.colSpan || 1;
            const widths = columnWidths.slice(columnIndex, columnIndex + span);
            if (widths.length > 0) {
                const totalWidth = widths.reduce(
                    (sum, value) => sum + value,
                    0,
                );
                (cell as HTMLTableCellElement).style.width = `${totalWidth}px`;
            }
            columnIndex += span;
        });
    }, [columnWidths, virtualizationCompatible]);

    const virtualizer = useVirtualizer({
        count: data.length,
        getScrollElement: () => scrollElementRef.current,
        estimateSize: () => 40,
        overscan: 5,
    });

    const measureRowRef = useCallback(
        (node: HTMLTableRowElement | null) => {
            virtualizer.measureElement(node ?? undefined);
        },
        [virtualizer],
    );

    const virtualItems = virtualizer.getVirtualItems();

    if (!virtualizationCompatible || data.length === 0) {
        return (
            <StyledTable>
                <thead>{header}</thead>
                <tbody>
                    {preRow}
                    {data.map((element, index) => body(element, index))}
                    {postRow}
                </tbody>
            </StyledTable>
        );
    }

    return (
        <div ref={containerRef} style={{ width: "100%", height: "100%" }}>
            <div
                style={{
                    position: "absolute",
                    top: -9999,
                    left: -9999,
                    visibility: "hidden",
                    pointerEvents: "none",
                }}
            >
                <StyledTable ref={measurementTableRef}>
                    <thead>{header}</thead>
                    <tbody>
                        {data.length > 0 && body(data[0], 0)}
                        {preRow}
                        {postRow}
                    </tbody>
                </StyledTable>
            </div>

            <div
                ref={scrollElementRef}
                style={{
                    height: containerHeight,
                    maxHeight: "100%",
                    overflow: "auto",
                    width: "100%",
                }}
            >
                <StyledTable
                    style={{
                        width: tableWidth || "auto",
                        tableLayout: tableWidth ? "fixed" : "auto",
                        position: "relative",
                    }}
                >
                    <thead
                        ref={headerRef}
                        style={{
                            position: "sticky",
                            top: 0,
                            zIndex: 10,
                            backgroundColor: "white",
                        }}
                    >
                        {header}
                    </thead>
                    <tbody
                        style={{
                            position: "relative",
                            height: virtualizer.getTotalSize(),
                        }}
                    >
                        {preRow &&
                            renderRowElement(preRow, {
                                style: {
                                    position: "absolute",
                                    top: 0,
                                    left: 0,
                                    width: "100%",
                                    zIndex: 1,
                                },
                            })}

                        {virtualItems.map((virtualItem, index) => {
                            const rowContent = body(
                                data[virtualItem.index],
                                virtualItem.index,
                            );

                            return renderRowElement(rowContent, {
                                key: virtualItem.key,
                                style: {
                                    position: "absolute",
                                    top:
                                        virtualItem.start - (index > 0 ? 1 : 0),
                                    left: 0,
                                    width: "100%",
                                },
                                "data-index": String(virtualItem.index),
                                ref: measureRowRef,
                            });
                        })}

                        {postRow &&
                            renderRowElement(postRow, {
                                style: {
                                    position: "absolute",
                                    top: virtualizer.getTotalSize(),
                                    left: 0,
                                    width: "100%",
                                    zIndex: 1,
                                },
                            })}
                    </tbody>
                </StyledTable>
            </div>
        </div>
    );
}

const Table = memo(TableComponent, "Table");

export default Table;
