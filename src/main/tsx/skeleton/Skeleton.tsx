import React from "react";

import Header from "@/skeleton/Header/Header";
import MainArea from "@/skeleton/MainArea/MainArea";
import Sidebar from "@/skeleton/Sidebar/Sidebar";
import StyledSkeleton from "@/skeleton/styles/StyledSkeleton";

const Skeleton: React.FC = () => {
    return (
        <StyledSkeleton>
            <Sidebar/>
            <Header/>
            <MainArea/>
        </StyledSkeleton>
    );
};

export default Skeleton;
