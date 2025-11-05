import React, { useEffect } from "react";

import { useAccounts } from "@/services/accounts";
import { useCategories } from "@/services/categories";
import { useTransactions } from "@/services/transactions";
import useHeader from "@/skeleton/Header/stores/useHeader";
import AccountList from "@/views/TrackingView/AccountList/AccountList";
import StyledTrackingView from "@/views/TrackingView/styles/StyledTrackingView";
import TransactionsTable from "@/views/TrackingView/TransactionsTable/TransactionsTable";

const TrackingView: React.FC = () => {
    const { setHeader } = useHeader();

    useEffect(() => {
        setHeader({});
    }, [setHeader]);

    const accounts = useAccounts();
    const categories = useCategories();
    const transactions = useTransactions();

    if (accounts.isLoading || categories.isLoading) return <div>Loading...</div>;
    if (accounts.error || categories.error)
        return <div>Error: {accounts.error?.message ?? categories.error?.message}</div>;
    if (!accounts.data || !categories.data) return <div>No data available</div>;

    return (
        <StyledTrackingView>
            <TransactionsTable
                accounts={accounts.data}
                categories={categories.data}
                transactions={transactions.data ?? []}
            />
            <AccountList />
        </StyledTrackingView>
    );
};

export default TrackingView;
