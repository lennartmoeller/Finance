import useItems, {UseItemsResult} from "@/services/util/useItems";
import Stats, {statsMapper} from "@/types/Stats";

export const statsUrl = new URL("api/stats", window.location.origin);

export const useStats = (): UseItemsResult<Stats> => useItems({
    url: statsUrl,
    converter: statsMapper.fromDTO,
});
