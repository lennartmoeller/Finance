import YearMonth from "@/utils/YearMonth";

interface TransactionFilters {
    accountIds: number[] | null;
    categoryIds: number[] | null;
    yearMonths: YearMonth | null;
    description: string | null;
}

export default TransactionFilters;
