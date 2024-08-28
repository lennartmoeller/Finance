import {useQuery} from "@tanstack/react-query";

import {toUrl} from "@/utils/url";

interface UseGetQueryOptions<Body, Data> {
    url: string;
    queryParams?: Record<string, string>;
    converter: (body: Body) => Data;
}

export interface UseGetQueryResult<Data> {
    data: Data | undefined;
    error: Error | undefined;
    isLoading: boolean;
}

const useGetQuery = <Body, Data>(options: UseGetQueryOptions<Body, Data>): UseGetQueryResult<Data> => {

    const url: string = toUrl(options.url, options.queryParams);

    const useQueryResult = useQuery({
        queryKey: [url],
        queryFn: async () => {
            const response: Response = await fetch(url);
            return await response.json();
        },
    });

    return {
        data: useQueryResult.data ? options.converter(useQueryResult.data) : undefined,
        error: useQueryResult.error ?? undefined,
        isLoading: useQueryResult.isLoading,
    };

};

export default useGetQuery;
