import React from "react";

import StyledSidebarLogoContainer from "@/skeleton/Sidebar/Logo/styles/StyledSidebarLogoContainer";

const StyledSidebarLogo: React.FC = () => {
    return (
        <StyledSidebarLogoContainer>
            <svg viewBox="0 0 603 902.84" xmlns="http://www.w3.org/2000/svg">
                <g fill="#01579b">
                    <path d="m301.51 601.49 299.99-149.99-299.99-149.99z"/>
                    <path d="m1.5 1.5v450l300-150h-.01v-150z"/>
                    <path d="m601.5 1.5h-300.01v150l300.01 150z"/>
                    <path d="m301.49 901.5v-300h.01l-300-150v300z"/>
                </g>
                <g fill="#54c5f8" opacity=".9">
                    <path d="m1.5 451.5v300l299.99-150v-300z"/>
                    <path d="m301.49 1.5v150l-299.99-150z"/>
                </g>
            </svg>
        </StyledSidebarLogoContainer>
    );
};

export default StyledSidebarLogo;
