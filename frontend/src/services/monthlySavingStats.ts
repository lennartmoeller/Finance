import useItems, { UseItemsResult } from "@/services/util/useItems";
import MonthlySavingStats, {
    MonthlySavingStatsDTO,
    monthlySavingStatsMapper,
} from "@/types/MonthlySavingStats";
import { ExtURL } from "@/utils/ExtURL";

export const monthlySavingStatsUrl = new ExtURL(
    "api/stats/monthlySavings",
    window.location.origin,
);

export const useMonthlySavingStats = (): UseItemsResult<
    Array<MonthlySavingStats>
> =>
    useItems({
        url: monthlySavingStatsUrl,
        converter: (dto: Array<MonthlySavingStatsDTO>) =>
            dto.map(monthlySavingStatsMapper.fromDTO),
    });
