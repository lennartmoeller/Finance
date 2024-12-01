import useItems, {UseItemsResult} from "@/services/util/useItems";
import MonthlyCategoryBalanceStats, {monthlyCategoryBalanceStatsMapper} from "@/types/MonthlyCategoryBalanceStats";
import {ExtURL} from "@/utils/ExtURL";

export const monthlyCategoryBalanceStatsUrl = new ExtURL("api/stats/monthlyCategoryBalances", window.location.origin);

export const useMonthlyCategoryBalanceStats = (): UseItemsResult<MonthlyCategoryBalanceStats> => useItems({
    url: monthlyCategoryBalanceStatsUrl,
    converter: monthlyCategoryBalanceStatsMapper.fromDTO,
});
