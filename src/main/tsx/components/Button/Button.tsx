import React from "react";

import StyledButton from "@/components/Button/styles/StyledButton";

export interface ButtonProps {
    onClick: () => void;
    children?: React.ReactNode;
}

const Button: React.FC<ButtonProps> = ({onClick, children}) => {
    return (
        <StyledButton onClick={onClick}>
            {children}
        </StyledButton>
    );
};

export default Button;
