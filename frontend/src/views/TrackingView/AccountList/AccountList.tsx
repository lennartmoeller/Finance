import React from "react";

import {useAccountBalances} from "@/services/accountBalances";
import {useAccounts} from "@/services/accounts";
import Account from "@/types/Account";
import AccountItem from "@/views/TrackingView/AccountList/AccountItem";
import StyledAccountList from "@/views/TrackingView/AccountList/styles/StyledAccountList";

const AccountList: React.FC = () => {
    const accounts = useAccounts();
    const accountBalances = useAccountBalances();

    if (accounts.isLoading || accountBalances.isLoading) return (
        <div>Loading...</div>
    );
    if (accounts.error || accountBalances.error) return (
        <div>Error: {accounts.error?.message ?? accountBalances.error?.message}</div>
    );
    if (!accounts.data || !accountBalances.data) return (
        <div>No data available</div>
    );

    return (
        <StyledAccountList>
            {accountBalances.data.map(({accountId, balance}) => {
                const account: Account = accounts.data!.find(a => a.id === accountId)!;
                return (
                    <AccountItem key={account.id} account={account} balance={balance}/>
                );
            })}
        </StyledAccountList>
    );

};

export default AccountList;
