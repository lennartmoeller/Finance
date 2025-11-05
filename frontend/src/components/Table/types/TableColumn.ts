import React from "react";

import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";

interface TableColumn<TData = unknown> {
    key: React.Key;
    width: number;
    header?: {
        name: string;
        props?: Record<string, unknown>;
    };
    filter?: {
        property: string;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        inputFormatter: InputFormatter<any>;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        filterFunction?: (filterValue: any, data: TData) => boolean;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        initialValue?: any;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        onChange?: (value: any) => void;
    };
}

export default TableColumn;
