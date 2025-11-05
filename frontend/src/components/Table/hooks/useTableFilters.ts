import { useCallback, useMemo, useState } from "react";

import useForm from "@/components/Form/hooks/useForm";
import TableColumn from "@/components/Table/types/TableColumn";

interface UseTableFiltersOptions<TData> {
    columns?: Array<TableColumn<TData>>;
}

const useTableFilters = <TData = unknown>({ columns }: UseTableFiltersOptions<TData>) => {
    const hasFilters = useMemo(() => columns?.some((column) => column.filter !== undefined), [columns]);

    const filterInitialValues = useMemo(
        () =>
            columns?.reduce(
                (acc, column) => {
                    if (column.filter) {
                        const key = column.filter.property;
                        acc[key] = column.filter.initialValue ?? null;
                    }
                    return acc;
                },
                {} as Record<string, unknown>,
            ) ?? {},
        [columns],
    );

    const [currentFilterValues, setCurrentFilterValues] = useState<Record<string, unknown>>(filterInitialValues);

    const onChangeCallbacks = useMemo(
        () =>
            columns?.reduce(
                (acc, column) => {
                    if (column.filter) {
                        const key = column.filter.property;
                        acc[key] = column.filter.onChange;
                    }
                    return acc;
                },
                {} as Record<string, ((value: unknown) => void) | undefined>,
            ),
        [columns],
    );

    const registerFilter = useForm<Record<string, unknown>>({
        initial: filterInitialValues,
        onSuccess: async (filters: Record<string, unknown>) => {
            setCurrentFilterValues(filters);

            // Call individual onChange callbacks for each filter
            Object.entries(filters).forEach(([key, value]) => {
                const callback = onChangeCallbacks?.[key];
                callback?.(value);
            });
        },
    });

    const filterData = useCallback(
        <T extends TData>(data: T[]): T[] => {
            if (!columns) return data;

            return data.filter((element) => {
                return columns.every((column) => {
                    if (!column.filter?.filterFunction) return true;

                    const filterValue = currentFilterValues[column.filter.property];

                    if (filterValue === null || filterValue === undefined) {
                        return true;
                    }

                    if (Array.isArray(filterValue) && filterValue.length === 0) {
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

export default useTableFilters;
