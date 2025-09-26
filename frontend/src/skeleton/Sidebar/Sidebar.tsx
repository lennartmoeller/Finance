import React from "react";

import { Link } from "react-router-dom";

import Icon from "@/components/Icon/Icon";
import routes from "@/skeleton/routes";
import StyledSidebarLogo from "@/skeleton/Sidebar/Logo/StyledSidebarLogo";
import StyledSidebar from "@/skeleton/Sidebar/styles/StyledSidebar";
import StyledSidebarButton from "@/skeleton/Sidebar/styles/StyledSidebarButton";
import StyledSidebarButtonLabel from "@/skeleton/Sidebar/styles/StyledSidebarButtonLabel";
import StyledSidebarLinks from "@/skeleton/Sidebar/styles/StyledSidebarLinks";

const Sidebar: React.FC = () => {
    return (
        <StyledSidebar>
            <StyledSidebarLogo />
            <StyledSidebarLinks>
                {Object.entries(routes).map(([path, { icon, label }]) => (
                    <Link key={path} to={path}>
                        <StyledSidebarButton>
                            <Icon
                                id={`fa-light ${icon}`}
                                opacity={0.7}
                                size={24}
                            />
                            <StyledSidebarButtonLabel>
                                {label}
                            </StyledSidebarButtonLabel>
                        </StyledSidebarButton>
                    </Link>
                ))}
            </StyledSidebarLinks>
        </StyledSidebar>
    );
};

export default Sidebar;
