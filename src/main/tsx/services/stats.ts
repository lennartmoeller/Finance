import useItems, {UseItemsResult} from "@/services/util/useItems";
import Stats, {statsMapper} from "@/types/Stats";
import {ExtURL} from "@/utils/ExtURL";

export const statsUrl = new ExtURL("api/stats", window.location.origin);

export const useStats = (): UseItemsResult<Stats> => useItems({
    url: statsUrl,
    converter: statsMapper.fromDTO,
});
