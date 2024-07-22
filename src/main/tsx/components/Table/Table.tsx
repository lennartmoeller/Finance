import React, {ReactNode} from "react";

import {StyledTable} from "@/components/Table/style";

interface TableProps<T> {
    data: Array<T>;
    header: ReactNode;
    body: (element: T, index: number) => ReactNode;
}

export const Table = <T, >({data, header, body}: TableProps<T>) => {
    return (
        <StyledTable>
            <thead>
            <tr>
                {header}
            </tr>
            </thead>
            <tbody>
            {data.map((element: T, index: number) => body(element, index))}
            </tbody>
        </StyledTable>
    );
};