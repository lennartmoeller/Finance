import {useEffect} from "react";

import {useQuery} from "@tanstack/react-query";

import endpoints from "@/api/endpoints";
import usePersistentState from "@/hooks/usePersistentState"; // Import the new hook
import EntityIdentifier from "@/types/EntityIdentifier";

type UseGetQueryResult<Data> = {
    data: Data | null;
    error: Error | null;
    isLoading: boolean;
};

const useCachedGetQuery = <Body, Data>(entity: EntityIdentifier, converter: (body: Body) => Data): UseGetQueryResult<Data> => {
    const endpoint: string = endpoints[entity];

    const [cachedData, setCachedData] = usePersistentState<Body | null>(endpoint, null);
    const convertedCachedData = cachedData ? converter(cachedData) : null;

    const queryResult = useQuery<Data, Error>({
        queryKey: [endpoint],
        queryFn: async (): Promise<Data> => {
            const response: Response = await fetch(endpoint);
            const body: Body = await response.json();
            setCachedData(body);
            return converter(body);
        },
    });

    useEffect(() => {
        if (queryResult.isSuccess && queryResult.data) {
            setCachedData(queryResult.data as Body);
        }
    }, [queryResult.isSuccess, queryResult.data, setCachedData]);

    return {
        data: convertedCachedData ?? queryResult.data ?? null,
        isLoading: !convertedCachedData && queryResult.isLoading,
        error: queryResult.isError ? queryResult.error : null,
    };
};

export default useCachedGetQuery;
