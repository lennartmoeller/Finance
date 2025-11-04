import { useCallback, useMemo, useState } from "react";

import useForm from "@/components/Form/hooks/useForm";
import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";

export interface TableColumn<TData = unknown> {
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
    };
}

interface UseTableFiltersOptions<TFilters extends object, TData> {
    columns?: Array<TableColumn<TData>>;
    initialFilterValues?: Partial<TFilters>;
    onFilterChange?: (filters: TFilters) => void;
}

export const useTableFilters = <
    TFilters extends object = Record<string, unknown>,
    TData = unknown,
>({
    columns,
    initialFilterValues = {} as TFilters,
    onFilterChange,
}: UseTableFiltersOptions<TFilters, TData>) => {
    const [currentFilterValues, setCurrentFilterValues] = useState<TFilters>(
        initialFilterValues as TFilters,
    );

    const hasFilters = useMemo(
        () => columns?.some((column) => column.filter !== undefined),
        [columns],
    );

    const filterInitialValues = useMemo(
        () =>
            columns?.reduce((acc, column) => {
                if (column.filter) {
                    const key = column.filter.property as keyof TFilters;
                    acc[key] =
                        (initialFilterValues?.[key] as
                            | TFilters[keyof TFilters]
                            | undefined) ?? (null as TFilters[keyof TFilters]);
                }
                return acc;
            }, {} as TFilters) ?? ({} as TFilters),
        [columns, initialFilterValues],
    );

    const registerFilter = useForm<TFilters>({
        initial: filterInitialValues,
        onSuccess: async (filters: TFilters) => {
            setCurrentFilterValues(filters);
            onFilterChange?.(filters);
        },
    });

    const filterData = useCallback(
        <T extends TData>(data: T[]): T[] => {
            if (!columns) return data;

            return data.filter((element) => {
                return columns.every((column) => {
                    if (!column.filter?.filterFunction) return true;

                    const filterValue =
                        currentFilterValues[
                            column.filter.property as keyof TFilters
                        ];

                    if (filterValue === null || filterValue === undefined) {
                        return true;
                    }

                    if (
                        Array.isArray(filterValue) &&
                        filterValue.length === 0
                    ) {
                        return true;
                    }

                    return column.filter.filterFunction(filterValue, element);
                });
            });
        },
        [columns, currentFilterValues],
    );

    return {
        hasFilters,
        registerFilter,
        filterData,
        currentFilterValues,
    };
};
