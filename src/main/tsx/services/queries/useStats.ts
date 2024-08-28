import useGetQuery, {UseGetQueryResult} from "@/services/queries/util/useGetQuery";
import Stats, {statsMapper} from "@/types/Stats";

const useStats = (): UseGetQueryResult<Stats> => useGetQuery({
    url: "/api/stats",
    converter: statsMapper.fromDTO,
});

export default useStats;
