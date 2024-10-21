import React from "react";

import {Outlet} from "react-router-dom";

import StyledHeaderDummy from "@/components/Skeleton/styles/StyledHeaderDummy";
import StyledMainArea from "@/components/Skeleton/styles/StyledMainArea";
import StyledSidebarDummy from "@/components/Skeleton/styles/StyledSidebarDummy";
import StyledSkeleton from "@/components/Skeleton/styles/StyledSkeleton";
import SidebarLinks from "@/routing/SidebarLinks";

const Skeleton: React.FC = () => {
    return <StyledSkeleton>
        <StyledSidebarDummy>
            <SidebarLinks/>
        </StyledSidebarDummy>
        <StyledHeaderDummy/>
        <StyledMainArea>
            <Outlet/>
        </StyledMainArea>
    </StyledSkeleton>;
};

export default Skeleton;
