import YearMonth from "@/utils/YearMonth";

interface TransactionFilters {
    accountIds: number | null;
    categoryIds: number | null;
    yearMonths: YearMonth | null;
}

export default TransactionFilters;
