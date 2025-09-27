import React, { ReactNode } from "react";

import StyledTable from "@/components/Table/styles/StyledTable";
import TableRow from "@/components/Table/TableRow";

interface TableRowGroup<T> {
    data: Array<T>;
    key: (element: T, index: number) => React.Key;
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
    return (
        <StyledTable>
            <thead>{header}</thead>
            <tbody>
                {pre?.data.map((element: TPre, index: number) => (
                    <TableRow
                        key={pre.key(element, index)}
                        {...pre.properties(element, index)}
                    >
                        {pre.content(element, index)}
                    </TableRow>
                ))}
                {body.data.map((element: TBody, index: number) => (
                    <TableRow
                        key={body.key(element, index)}
                        {...body.properties(element, index)}
                    >
                        {body.content(element, index)}
                    </TableRow>
                ))}
                {post?.data.map((element: TPost, index: number) => (
                    <TableRow
                        key={post.key(element, index)}
                        {...post.properties(element, index)}
                    >
                        {post.content(element, index)}
                    </TableRow>
                ))}
            </tbody>
        </StyledTable>
    );
};

export default Table;
