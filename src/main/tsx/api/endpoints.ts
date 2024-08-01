import EntityIdentifier from "@/types/EntityIdentifier";

const endpoints: Record<EntityIdentifier, string> = {
    accounts: '/api/accounts',
    categories: '/api/categories',
    stats: '/api/stats',
    transactions: '/api/transactions',
};

export default endpoints;
