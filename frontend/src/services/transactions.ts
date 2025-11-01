import { accountBalancesUrl } from "@/services/accountBalances";
import { monthlyCategoryBalanceStatsUrl } from "@/services/monthlyCategoryBalanceStats";
import useDeleteItem, {
    UseDeleteItemResult,
} from "@/services/util/useDeleteItem";
import useItems, { UseItemsResult } from "@/services/util/useItems";
import useSaveItem, { UseSaveItemResult } from "@/services/util/useSaveItem";
import Transaction, {
    TransactionDTO,
    transactionMapper,
} from "@/types/Transaction";
import { ExtURL } from "@/utils/ExtURL";

export const transactionsUrl = new ExtURL(
    "api/transactions",
    window.location.origin,
);

export const useTransactions = (): UseItemsResult<Array<Transaction>> => {
    return useItems({
        url: transactionsUrl,
        converter: (body: Array<TransactionDTO>) =>
            body.map(transactionMapper.fromDTO),
    });
};

export const useSaveTransaction = (): UseSaveItemResult<Transaction> => {
    return useSaveItem({
        url: transactionsUrl,
        converter: transactionMapper.toDTO,
        invalidateQueryUrls: () => [
            transactionsUrl,
            accountBalancesUrl,
            monthlyCategoryBalanceStatsUrl,
        ],
    });
};

export const useDeleteTransaction = (): UseDeleteItemResult<Transaction> => {
    return useDeleteItem({
        url: transactionsUrl,
        invalidateQueryUrls: () => [
            transactionsUrl,
            accountBalancesUrl,
            monthlyCategoryBalanceStatsUrl,
        ],
    });
};
