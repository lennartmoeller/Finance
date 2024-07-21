import {endpoints} from "@/api/endpoints";
import {EntityIdentifier} from "@/types/EntityIdentifier";
import {useQuery, UseQueryResult} from "@tanstack/react-query";

export function useGetQuery<Body>(entity: EntityIdentifier): UseQueryResult<Body, Error> {
    const endpoint: string = endpoints[entity];
    return useQuery<Body, Error>({
        queryKey: [endpoint],
        queryFn: async (): Promise<Body> => {
            const response: Response = await fetch(endpoint);
            return await response.json();
        },
    })
}
