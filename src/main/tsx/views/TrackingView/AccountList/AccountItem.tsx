import React from "react";

import Account from "@/types/Account";
import {getEuroString} from "@/utils/money";
import StyledAccountItem from "@/views/TrackingView/AccountList/styles/StyledAccountItem";
import StyledAccountItemBalance from "@/views/TrackingView/AccountList/styles/StyledAccountItemBalance";
import StyledAccountItemLabel from "@/views/TrackingView/AccountList/styles/StyledAccountItemLabel";

interface AccountProps {
    account: Account;
    balance: number;
}

const AccountItem: React.FC<AccountProps> = ({account, balance}: AccountProps) => {

    return (
        <StyledAccountItem>
            <StyledAccountItemLabel>
                {account.label}
            </StyledAccountItemLabel>
            <StyledAccountItemBalance>
                {getEuroString(balance)}
            </StyledAccountItemBalance>
        </StyledAccountItem>
    );
};

export default AccountItem;
