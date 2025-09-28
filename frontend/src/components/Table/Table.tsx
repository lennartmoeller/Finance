import React, { ReactNode, useRef } from "react";

import { useVirtualizer } from "@tanstack/react-virtual";

import StyledTable from "@/components/Table/styles/StyledTable";

interface TableRowGroup<T> {
    data: Array<T>;
    content: (element: T, index: number) => ReactNode;
    properties: (
        element: T,
        index: number,
    ) => React.HTMLAttributes<HTMLTableRowElement>;
}

interface TableProps<TBody, TPre, TPost> {
    header: ReactNode;
    body: TableRowGroup<TBody>;
    pre?: TableRowGroup<TPre>;
    post?: TableRowGroup<TPost>;
}

const Table = <TBody, TPre, TPost>({
    header,
    body,
    pre,
    post,
}: TableProps<TBody, TPre, TPost>) => {
    const parentRef = useRef<HTMLDivElement>(null);

    const mapGroup = <T,>(group?: TableRowGroup<T>) =>
        group?.data.map((element, index) => ({
            content: group.content(element, index),
            properties: group.properties(element, index),
        })) ?? [];
    const rowData = [...mapGroup(pre), ...mapGroup(body), ...mapGroup(post)];

    const virtualizer = useVirtualizer({
        count: rowData.length,
        getScrollElement: () => parentRef.current,
        estimateSize: () => 50,
    });

    return (
        <div ref={parentRef} style={{ overflow: "auto", height: "400px" }}>
            <StyledTable>
                <thead>{header}</thead>
                <tbody
                    style={{
                        height: `${virtualizer.getTotalSize()}px`,
                        position: "relative",
                    }}
                >
                    {virtualizer.getVirtualItems().map((virtualRow) => {
                        const data = rowData[virtualRow.index];
                        return (
                            <tr
                                key={virtualRow.key}
                                data-index={virtualRow.index}
                                ref={virtualizer.measureElement}
                                style={{
                                    position: "absolute",
                                    top: 0,
                                    left: 0,
                                    width: "100%",
                                    transform: `translateY(${virtualRow.start}px)`,
                                    display: "table-row-group",
                                }}
                                {...data.properties}
                            >
                                {data.content}
                            </tr>
                        );
                    })}
                </tbody>
            </StyledTable>
        </div>
    );
};

export default Table;
