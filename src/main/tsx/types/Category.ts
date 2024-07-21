import {CategorySmoothType} from "@/types/CategorySmoothType";
import {TransactionType} from "@/types/TransactionType";

export interface Category {
    id: number;
    parentId: number;
    label: string;
    transactionType: TransactionType;
    smoothType: CategorySmoothType;
    start: Date;
    end: Date;
    target: number;
}
