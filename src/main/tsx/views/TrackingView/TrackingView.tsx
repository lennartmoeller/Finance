import React from "react";

import AccountList from "@/views/TrackingView/AccountList/AccountList";
import StyledAccountsListContainer from "@/views/TrackingView/styles/StyledAccountsListContainer";
import StyledTrackingView from "@/views/TrackingView/styles/StyledTrackingView";
import StyledTransactionsTableContainer from "@/views/TrackingView/styles/StyledTransactionsTableContainer";
import StyledYearMonthSelectorContainer from "@/views/TrackingView/styles/StyledYearMonthSelectorContainer";
import TransactionsTable from "@/views/TrackingView/TransactionsTable/TransactionsTable";
import YearMonthSelector from "@/views/TrackingView/YearMonthSelector";

const TrackingView: React.FC = () => {
    return (
        <StyledTrackingView>
            <StyledYearMonthSelectorContainer>
                <YearMonthSelector/>
            </StyledYearMonthSelectorContainer>
            <StyledTransactionsTableContainer>
                <TransactionsTable/>
            </StyledTransactionsTableContainer>
            <StyledAccountsListContainer>
                <AccountList/>
            </StyledAccountsListContainer>
        </StyledTrackingView>
    );

};

export default TrackingView;
