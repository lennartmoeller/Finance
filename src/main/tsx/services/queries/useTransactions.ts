import useGetQuery, {UseGetQueryResult} from "@/services/queries/util/useGetQuery";
import selectedYearMonthStore from "@/stores/selectedYearMonthStore";
import Transaction, {TransactionDTO, transactionMapper} from "@/types/Transaction";

const useTransactions = (): UseGetQueryResult<Array<Transaction>> => {
    const {selectedYearMonth} = selectedYearMonthStore();

    return useGetQuery({
        url: "/api/transactions",
        converter: (body: Array<TransactionDTO>) => body.map(transactionMapper.fromDTO),
        queryParams: {
            yearMonth: selectedYearMonth.toString()
        },
    });
};

export default useTransactions;
