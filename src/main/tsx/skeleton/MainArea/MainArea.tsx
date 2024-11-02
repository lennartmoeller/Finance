import React from "react";

import {Outlet} from "react-router-dom";

import StyledMainArea from "@/skeleton/MainArea/styles/StyledMainArea";
import StyledMainAreaInner from "@/skeleton/MainArea/styles/StyledMainAreaInner";

const Sidebar: React.FC = () => {
    return (
        <StyledMainArea>
            <StyledMainAreaInner>
                <Outlet/>
            </StyledMainAreaInner>
        </StyledMainArea>
    );
};

export default Sidebar;
