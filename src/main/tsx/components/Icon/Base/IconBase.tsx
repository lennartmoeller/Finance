import React from "react";

import StyledIcon from "@/components/Icon/Base/styles/StyledIcon";

export interface IconProps {
    rotation?: number;
    size?: number;
    opacity?: number;
    children?: React.ReactNode;
    color?: string;
}

const IconBase: React.FC<IconProps> = (
    {
        color,
        opacity,
        rotation,
        size = 12,
        children
    }
) => {
    return (
        <StyledIcon
            $color={color}
            $opacity={opacity}
            $rotation={rotation}
            $size={size}>
            {children}
        </StyledIcon>
    );
};

export default IconBase;
