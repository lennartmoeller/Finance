import {CategorySmoothType} from "@/types/CategorySmoothType";
import {TransactionType} from "@/types/TransactionType";

export interface CategoryDTO {
    id: number;
    parentId: number;
    label: string;
    transactionType: TransactionType;
    smoothType: CategorySmoothType;
    start: string;
    end: string;
    target: number;
}
