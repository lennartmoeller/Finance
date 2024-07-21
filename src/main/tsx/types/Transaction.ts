export interface Transaction {
    id: number;
    accountId: number;
    categoryId: number;
    date: Date;
    amount: number;
    description: string;
}
