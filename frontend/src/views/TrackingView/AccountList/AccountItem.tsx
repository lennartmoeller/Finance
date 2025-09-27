import React from "react";

import Account from "@/types/Account";
import { getEuroString } from "@/utils/money";
import { memo } from "@/utils/react";
import StyledAccountItem from "@/views/TrackingView/AccountList/styles/StyledAccountItem";
import StyledAccountItemBalance from "@/views/TrackingView/AccountList/styles/StyledAccountItemBalance";
import StyledAccountItemLabel from "@/views/TrackingView/AccountList/styles/StyledAccountItemLabel";

interface AccountProps {
    account: Account;
    balance: number;
    isSelected: boolean;
}

function AccountItemComponent({ account, balance, isSelected }: AccountProps) {
    return (
        <StyledAccountItem $isSelected={isSelected}>
            <StyledAccountItemLabel>{account.label}</StyledAccountItemLabel>
            <StyledAccountItemBalance>
                {getEuroString(balance)}
            </StyledAccountItemBalance>
        </StyledAccountItem>
    );
}

const AccountItem = memo(AccountItemComponent, "AccountItem");

export default AccountItem;
