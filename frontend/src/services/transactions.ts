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
import YearMonth from "@/utils/YearMonth";
import useTransactionFilter from "@/views/TrackingView/stores/useTransactionFilter";

export const transactionsUrl = new ExtURL(
    "api/transactions",
    window.location.origin,
);

export const useTransactions = (): UseItemsResult<Array<Transaction>> => {
    const { accountIds, categoryIds, yearMonths } = useTransactionFilter();

    const url = new ExtURL(transactionsUrl.toString());
    url.setSearchParams({
        accountIds: accountIds.join(","),
        categoryIds: categoryIds.join(","),
        yearMonths: yearMonths.map(YearMonth.toString).join(","),
    });

    return useItems({
        url,
        converter: (body: Array<TransactionDTO>) =>
            body.map(transactionMapper.fromDTO),
    });
};

export const useSaveTransaction = (): UseSaveItemResult<Transaction> => {
    const { accountIds, categoryIds, yearMonths } = useTransactionFilter();

    return useSaveItem({
        url: transactionsUrl,
        converter: transactionMapper.toDTO,
        invalidateQueryUrls: (transaction: Transaction) => {
            // transactions currently displayed
            const currentTransactionsUrl = new ExtURL(
                transactionsUrl.toString(),
            );
            currentTransactionsUrl.setSearchParams({
                accountIds: accountIds.join(","),
                categoryIds: categoryIds.join(","),
                yearMonths: yearMonths.map(YearMonth.toString).join(","),
            });

            // target month of the transaction
            const targetTransactionsUrl = new ExtURL(
                transactionsUrl.toString(),
            );
            targetTransactionsUrl.setSearchParam(
                "yearMonth",
                YearMonth.fromDate(transaction.date).toString(),
            );

            return [
                currentTransactionsUrl,
                targetTransactionsUrl,
                accountBalancesUrl,
                monthlyCategoryBalanceStatsUrl,
            ];
        },
    });
};

export const useDeleteTransaction = (): UseDeleteItemResult<Transaction> => {
    return useDeleteItem({
        url: transactionsUrl,
        invalidateQueryUrls: (transaction: Transaction) => {
            // month of the transaction
            const url = new ExtURL(transactionsUrl, window.location.href);
            url.setSearchParam(
                "yearMonth",
                YearMonth.fromDate(transaction.date).toString(),
            );

            return [url, accountBalancesUrl, monthlyCategoryBalanceStatsUrl];
        },
    });
};
