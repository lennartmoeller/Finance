import { useQuery } from "@tanstack/react-query";

import axios from "@/services/util/axios";
import { ExtURL } from "@/utils/ExtURL";

export interface UseItemsOptions<Body, Data> {
    url: ExtURL;
    converter: (body: Body) => Data;
}

export interface UseItemsResult<Data> {
    data: Data | undefined;
    error: Error | undefined;
    isLoading: boolean;
}

const useItems = <Body, Data>(
    options: UseItemsOptions<Body, Data>,
): UseItemsResult<Data> => {
    const urlString: string = options.url.toString();

    const useQueryResult = useQuery({
        queryKey: [urlString],
        queryFn: async () => {
            const response = await axios.get(urlString);
            return response.data;
        },
    });

    return {
        data: useQueryResult.data
            ? options.converter(useQueryResult.data)
            : undefined,
        error: useQueryResult.error ?? undefined,
        isLoading: useQueryResult.isLoading,
    };
};

export default useItems;
