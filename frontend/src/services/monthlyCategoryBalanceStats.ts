import useItems, { UseItemsResult } from "@/services/util/useItems";
import MonthlyCategoryStats, { monthlyCategoryStatsMapper } from "@/types/MonthlyCategoryStats";
import { ExtURL } from "@/utils/ExtURL";

export const monthlyCategoryBalanceStatsUrl = new ExtURL("api/stats/monthlyCategoryBalances", window.location.origin);

export const useMonthlyCategoryBalanceStats = (): UseItemsResult<MonthlyCategoryStats> =>
    useItems({
        url: monthlyCategoryBalanceStatsUrl,
        converter: monthlyCategoryStatsMapper.fromDTO,
    });
