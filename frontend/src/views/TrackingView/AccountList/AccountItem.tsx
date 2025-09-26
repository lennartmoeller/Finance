import React from "react";

import Account from "@/types/Account";
import { getEuroString } from "@/utils/money";
import StyledAccountItem from "@/views/TrackingView/AccountList/styles/StyledAccountItem";
import StyledAccountItemBalance from "@/views/TrackingView/AccountList/styles/StyledAccountItemBalance";
import StyledAccountItemLabel from "@/views/TrackingView/AccountList/styles/StyledAccountItemLabel";

interface AccountProps {
    account: Account;
    balance: number;
    isSelected: boolean;
}

const AccountItem: React.FC<AccountProps> = React.memo(({
    account,
    balance,
    isSelected,
}: AccountProps) => {
    return (
        <StyledAccountItem $isSelected={isSelected}>
            <StyledAccountItemLabel>{account.label}</StyledAccountItemLabel>
            <StyledAccountItemBalance>
                {getEuroString(balance)}
            </StyledAccountItemBalance>
        </StyledAccountItem>
    );
});

AccountItem.displayName = 'AccountItem';

export default AccountItem;
