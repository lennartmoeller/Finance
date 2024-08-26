import {useQuery, UseQueryResult} from "@tanstack/react-query";

import endpoints from "@/api/endpoints";
import EntityIdentifier from "@/types/EntityIdentifier";

const useCachedGetQuery = <Body, Data>(
    entity: EntityIdentifier,
    converter: (body: Body) => Data,
    queryParams: Record<string, string> = {}
): UseQueryResult<Data, Error> => {
    const queryString = new URLSearchParams(queryParams).toString();
    const url: string = endpoints[entity] + (queryString ? `?${queryString}` : '');

    return useQuery({
        queryKey: [url],
        queryFn: async () => {
            const response: Response = await fetch(url);
            const body: Body = await response.json();
            const data: Data = converter(body);
            localStorage.setItem(url, JSON.stringify(body));
            return data;
        },
        // @ts-ignore I don't understand this typing error.
        placeholderData: () => {
            const cachedItem: string | null = localStorage.getItem(url);
            const cached: Body | null = cachedItem ? JSON.parse(cachedItem) as Body : null;
            return cached ? converter(cached) : undefined;
        }
    });
};

export default useCachedGetQuery;
