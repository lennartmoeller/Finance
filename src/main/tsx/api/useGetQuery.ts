import {useQuery, UseQueryResult} from "@tanstack/react-query";

import {endpoints} from "@/api/endpoints";
import {EntityIdentifier} from "@/types/EntityIdentifier";

export function useGetQuery<Body, Data>(entity: EntityIdentifier, converter?: (body: Body) => Data): UseQueryResult<Data, Error> {
    const endpoint: string = endpoints[entity];
    return useQuery<Data, Error>({
        queryKey: [endpoint],
        queryFn: async (): Promise<Data> => {
            const response: Response = await fetch(endpoint);
            const body: Body = await response.json();
            return converter ? converter(body) : body as unknown as Data;
        },
    });
}
