import useItems, {UseItemsResult} from "@/services/util/useItems";
import DailyBalanceStats, {DailyBalanceStatsDTO, dailyBalanceStatsMapper} from "@/types/DailyBalanceStats";
import {ExtURL} from "@/utils/ExtURL";

export const dailyBalanceStats = new ExtURL("api/stats/dailyBalances", window.location.origin);

export const useDailyBalanceStats = (): UseItemsResult<Array<DailyBalanceStats>> => useItems({
    url: dailyBalanceStats,
    converter: (dto: Array<DailyBalanceStatsDTO>) => dto.map(dailyBalanceStatsMapper.fromDTO),
});
