import {accountBalancesUrl} from "@/services/accountBalances";
import {statsUrl} from "@/services/stats";
import useDeleteItem, {UseDeleteItemResult} from "@/services/util/useDeleteItem";
import useItems, {UseItemsResult} from "@/services/util/useItems";
import useSaveItem, {UseSaveItemResult} from "@/services/util/useSaveItem";
import selectedYearMonthStore from "@/stores/selectedYearMonthStore";
import Transaction, {TransactionDTO, transactionMapper} from "@/types/Transaction";
import YearMonth from "@/utils/YearMonth";

export const transactionsUrl = new URL("api/transactions", window.location.origin);

export const useTransactions = (): UseItemsResult<Array<Transaction>> => {
    const {selectedYearMonth} = selectedYearMonthStore();

    const url = new URL(transactionsUrl.href);
    url.searchParams.set("yearMonth", selectedYearMonth.toString());

    return useItems({
        url,
        converter: (body: Array<TransactionDTO>) => body.map(transactionMapper.fromDTO),
    });
};

export const useSaveTransaction = (): UseSaveItemResult<Transaction> => {
    const {selectedYearMonth} = selectedYearMonthStore();

    return useSaveItem({
        url: transactionsUrl,
        converter: transactionMapper.toDTO,
        invalidateQueryUrls: (transaction: Transaction) => {
            // transactions currently displayed
            const currentTransactionsUrl = new URL(transactionsUrl.href);
            currentTransactionsUrl.searchParams.set("yearMonth", selectedYearMonth.toString());

            // target month of the transaction
            const targetTransactionsUrl = new URL(transactionsUrl.href);
            targetTransactionsUrl.searchParams.set("yearMonth", YearMonth.fromDate(transaction.date).toString());

            return [
                currentTransactionsUrl,
                targetTransactionsUrl,
                accountBalancesUrl,
                statsUrl,
            ];
        },
    });
};

export const useDeleteTransaction = (): UseDeleteItemResult<Transaction> => {
    return useDeleteItem({
        url: transactionsUrl,
        invalidateQueryUrls: (transaction: Transaction) => {
            // month of the transaction
            const url = new URL(transactionsUrl, window.location.href);
            url.searchParams.set("yearMonth", YearMonth.fromDate(transaction.date).toString());

            return [
                url,
                accountBalancesUrl,
                statsUrl,
            ];
        },
    });
};
