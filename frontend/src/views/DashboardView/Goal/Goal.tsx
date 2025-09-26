import React from "react";

import PerformanceCircle from "@/components/PerformanceCircle/PerformanceCircle";
import StyledGoalContainer from "@/views/DashboardView/Goal/styles/StyledGoalContainer";
import StyledGoalLabel from "@/views/DashboardView/Goal/styles/StyledGoalLabel";
import StyledGoalLabels from "@/views/DashboardView/Goal/styles/StyledGoalLabels";
import StyledGoalSublabel from "@/views/DashboardView/Goal/styles/StyledGoalSublabel";

interface GoalProps {
    performance: number;
    label: string;
    sublabel: string;
}

const Goal: React.FC<GoalProps> = ({ performance, label, sublabel }) => {
    return (
        <StyledGoalContainer>
            <PerformanceCircle performance={performance} />
            <StyledGoalLabels>
                <StyledGoalLabel>{label}</StyledGoalLabel>
                <StyledGoalSublabel>{sublabel}</StyledGoalSublabel>
            </StyledGoalLabels>
        </StyledGoalContainer>
    );
};

export default Goal;
