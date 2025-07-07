import React, {useEffect} from "react";

import useHeader from "@/skeleton/Header/stores/useHeader";
import BalanceChangeDeviationChart from "@/views/DashboardView/Charts/BalanceChangeDeviationChart";
import InflationCompensationChart from "@/views/DashboardView/Charts/InflationCompensationChart";
import InvestmentRevenueChart from "@/views/DashboardView/Charts/InvestmentRevenueChart";
import TotalAssetsChart from "@/views/DashboardView/Charts/TotalAssetsChart";
import TotalDepositsChart from "@/views/DashboardView/Charts/TotalDepositsChart";
import MonthStats from "@/views/DashboardView/MonthStats";
import StyledDiagramContainer from "@/views/DashboardView/styles/StyledDiagramContainer";
import StyledDiagramGrid from "@/views/DashboardView/styles/StyledDiagramGrid";

const DashboardView: React.FC = () => {
    const {setHeader} = useHeader();

    useEffect(() => {
        setHeader({});
    }, [setHeader]);

    return (
        <StyledDiagramGrid>
            <StyledDiagramContainer>
                <TotalAssetsChart/>
            </StyledDiagramContainer>
            <StyledDiagramContainer>
                <InflationCompensationChart/>
            </StyledDiagramContainer>
            <StyledDiagramContainer>
                <TotalDepositsChart/>
            </StyledDiagramContainer>
            <StyledDiagramContainer>
                <MonthStats/>
            </StyledDiagramContainer>
            <StyledDiagramContainer>
                <InvestmentRevenueChart/>
            </StyledDiagramContainer>
            <StyledDiagramContainer>
                <BalanceChangeDeviationChart/>
            </StyledDiagramContainer>
        </StyledDiagramGrid>
    );
};

export default DashboardView;
