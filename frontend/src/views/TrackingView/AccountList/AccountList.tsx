import React from "react";

import { useAccountBalances } from "@/services/accountBalances";
import { useAccounts } from "@/services/accounts";
import AccountItem from "@/views/TrackingView/AccountList/AccountItem";
import StyledAccountList from "@/views/TrackingView/AccountList/styles/StyledAccountList";
import useFocusedTransaction from "@/views/TrackingView/stores/useFocusedTransaction";
import useTransactionFilter from "@/views/TrackingView/stores/useTransactionFilter";

const AccountList: React.FC = () => {
    const accounts = useAccounts();
    const accountBalances = useAccountBalances();
    const { accountIds } = useTransactionFilter();
    const { focusedTransaction } = useFocusedTransaction();

    if (accounts.isLoading || accountBalances.isLoading) return <div>Loading...</div>;
    if (accounts.error || accountBalances.error)
        return <div>Error: {accounts.error?.message ?? accountBalances.error?.message}</div>;
    if (!accounts.data || !accountBalances.data) return <div>No data available</div>;

    const accountMap = new Map(accounts.data.map((account) => [account.id, account]));

    return (
        <StyledAccountList>
            {accountBalances.data.map(({ accountId, balance }) => {
                const account = accountMap.get(accountId)!;
                const isSelected = focusedTransaction
                    ? focusedTransaction.accountId === accountId
                    : accountIds.includes(accountId);
                return <AccountItem key={account.id} account={account} balance={balance} isSelected={isSelected} />;
            })}
        </StyledAccountList>
    );
};

export default AccountList;
