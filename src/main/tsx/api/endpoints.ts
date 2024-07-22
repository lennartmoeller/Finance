import {EntityIdentifier} from "@/types/EntityIdentifier";

export const endpoints: Record<EntityIdentifier, string> = {
    accounts: '/api/accounts',
    categories: '/api/categories',
    stats: '/api/stats',
    transactions: '/api/transactions',
};
