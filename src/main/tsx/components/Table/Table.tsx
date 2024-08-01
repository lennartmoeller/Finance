import React, {ReactNode} from "react";

import StyledTable from "@/components/Table/styles/StyledTable";

interface TableProps<T> {
    data: Array<T>;
    header: ReactNode;
    body: (element: T, index: number) => ReactNode;
}

const Table = <T, >({data, header, body}: TableProps<T>) => {
    return (
        <StyledTable>
            <thead>
            {header}
            </thead>
            <tbody>
            {data.map((element: T, index: number) => body(element, index))}
            </tbody>
        </StyledTable>
    );
};

export default Table;
