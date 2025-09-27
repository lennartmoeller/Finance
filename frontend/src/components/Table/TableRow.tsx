import React, { forwardRef, type ReactNode } from "react";

type TableRowProps = React.HTMLAttributes<HTMLTableRowElement> & {
    children: ReactNode;
};

const TableRow = forwardRef<HTMLTableRowElement, TableRowProps>(
    ({ children, ...rest }, ref) => {
        return (
            <tr ref={ref} {...rest}>
                {children}
            </tr>
        );
    },
);

TableRow.displayName = "TableRow";

export default TableRow;
