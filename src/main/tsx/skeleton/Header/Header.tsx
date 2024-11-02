import React from "react";

import {useLocation} from "react-router-dom";

import Icon from "@/components/Icon/Icon";
import useHeader from "@/skeleton/Header/stores/useHeader";
import StyledActions from "@/skeleton/Header/styles/StyledActions";
import StyledHeader from "@/skeleton/Header/styles/StyledHeader";
import StyledTitle from "@/skeleton/Header/styles/StyledTitle";
import routes, {Route} from "@/skeleton/routes";

const Header: React.FC = () => {
    const {headline, actions} = useHeader();

    const location = useLocation();
    const route: Route | undefined = routes[location.pathname] ?? undefined;

    return (
        <StyledHeader>
            <Icon
                key={route?.icon}
                id={`fa-light ${route?.icon}`}
                opacity={.7}
                size={28}
            />
            <StyledTitle>
                {headline ?? route?.label ?? ''}
            </StyledTitle>
            {actions && (
                <StyledActions>
                    {actions}
                </StyledActions>
            )}
        </StyledHeader>
    );
};

export default Header;
