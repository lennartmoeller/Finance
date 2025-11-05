import { useMutation, useQueryClient } from "@tanstack/react-query";

import axios from "@/services/util/axios";
import { filterDuplicates } from "@/utils/array";
import { ExtURL } from "@/utils/ExtURL";

export interface UseSaveItemOptions<Body, Item> {
    url: ExtURL;
    converter: (item: Item) => Body;
    invalidateQueryUrls?: Array<ExtURL> | ((item: Item) => Array<ExtURL> | undefined);
}

export type UseSaveItemResult<T> = (item: T) => Promise<void>;

const useSaveItem = <Body, Item>(options: UseSaveItemOptions<Body, Item>): UseSaveItemResult<Item> => {
    const queryClient = useQueryClient();

    const useSaveItemResult = useMutation({
        mutationFn: async (item: Item) => {
            const body: Body = options.converter(item);
            const response = await axios.post(options.url.toString(), body);
            return response.data;
        },
        onSuccess: async (_, item: Item) => {
            const queryUrls: Array<ExtURL> | undefined =
                typeof options.invalidateQueryUrls === "function"
                    ? options.invalidateQueryUrls(item)
                    : options.invalidateQueryUrls;
            for (const queryUrl of filterDuplicates(queryUrls ?? [])) {
                await queryClient.invalidateQueries({
                    queryKey: [queryUrl.toString()],
                });
            }
        },
        onError: (error) => {
            console.error("Error when saving item", error);
        },
    });

    return async (item: Item) => {
        await useSaveItemResult.mutateAsync(item);
    };
};

export default useSaveItem;
