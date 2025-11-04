import React, { ReactNode } from "react";

import StyledTable from "@/components/Table/styles/StyledTable";
import { memo } from "@/utils/react";

interface TableProps<T> {
    data: Array<T>;
    header: ReactNode;
    body: (element: T, index: number) => ReactNode;
    preRow?: ReactNode;
    postRow?: ReactNode;
}

interface TableHeaderProps {
    header: ReactNode;
}

interface TableBodyProps<T> {
    data: Array<T>;
    body: (element: T, index: number) => ReactNode;
    preRow?: ReactNode;
    postRow?: ReactNode;
}

function TableHeaderComponent({ header }: Readonly<TableHeaderProps>) {
    return <thead>{header}</thead>;
}
const TableHeader = memo(TableHeaderComponent, "TableHeader");

function TableBodyComponent<T>({
    data,
    body,
    preRow,
    postRow,
}: Readonly<TableBodyProps<T>>) {
    return (
        <tbody>
            {preRow}
            {data.map((element, index) => body(element, index))}
            {postRow}
        </tbody>
    );
}
const TableBody = memo(TableBodyComponent, "TableBody");

function TableComponent<T>({
    data,
    header,
    body,
    preRow,
    postRow,
}: Readonly<TableProps<T>>) {
    return (
        <StyledTable>
            <TableHeader header={header} />
            <TableBody
                data={data}
                body={body}
                preRow={preRow}
                postRow={postRow}
            />
        </StyledTable>
    );
}
const Table = memo(TableComponent, "Table");

export default Table;
