import React, {ReactNode} from "react";

import StyledTable from "@/components/Table/styles/StyledTable";

interface TableProps<T> {
    data: Array<T>;
    header: ReactNode;
    body: (element: T, index: number) => ReactNode;
    preRow?: ReactNode;
    postRow?: ReactNode;
}

const Table = <T, >({data, header, body, preRow, postRow}: TableProps<T>) => {
    return (
        <StyledTable>
            <thead>
            {header}
            </thead>
            <tbody>
            {preRow}
            {data.map((element: T, index: number) => body(element, index))}
            {postRow}
            </tbody>
        </StyledTable>
    );
};

export default Table;
