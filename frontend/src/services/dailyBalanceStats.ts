import useItems, { UseItemsResult } from "@/services/util/useItems";
import DailySavingStats, { DailySavingStatsDTO, dailySavingStatsMapper } from "@/types/DailySavingStats";
import { ExtURL } from "@/utils/ExtURL";

export const dailyBalanceStats = new ExtURL("api/stats/dailyBalances", window.location.origin);

export const useDailyBalanceStats = (): UseItemsResult<Array<DailySavingStats>> =>
    useItems({
        url: dailyBalanceStats,
        converter: (dto: Array<DailySavingStatsDTO>) => dto.map(dailySavingStatsMapper.fromDTO),
    });
