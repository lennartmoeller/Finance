import {useMutation, useQueryClient} from "@tanstack/react-query";

import axios from "@/services/util/axios";
import {filterDuplicates} from "@/utils/array";

export interface UseDeleteItemOptions<Item> {
    url: URL;
    invalidateQueryUrls?: Array<URL> | ((item: Item) => Array<URL> | undefined);
}

export type UseDeleteItemResult<T> = (item: T) => Promise<void>;

const useDeleteItem = <Item extends { id: number }>(options: UseDeleteItemOptions<Item>): UseDeleteItemResult<Item> => {
    const queryClient = useQueryClient();

    const useDeleteItemResult = useMutation({
        mutationFn: async (item: Item) => {
            return await axios.delete(`${options.url.toString()}/${item.id}`);
        },
        onSuccess: async (_, item: Item) => {
            const queryUrls: Array<URL> | undefined = typeof options.invalidateQueryUrls === "function" ? options.invalidateQueryUrls(item) : options.invalidateQueryUrls;
            for (const queryUrl of filterDuplicates(queryUrls ?? [])) {
                await queryClient.invalidateQueries({queryKey: [queryUrl.toString()]});
            }
        },
        onError: (error) => {
            console.error("Error when deleting item", error);
        },
    });

    return async (item: Item) => {
        await useDeleteItemResult.mutateAsync(item);
    };
};

export default useDeleteItem;