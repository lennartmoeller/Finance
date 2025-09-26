import React from "react";

import { Outlet } from "react-router-dom";

import StyledMainArea from "@/skeleton/MainArea/styles/StyledMainArea";

const Sidebar: React.FC = () => {
    return (
        <StyledMainArea>
            <Outlet />
        </StyledMainArea>
    );
};

export default Sidebar;
