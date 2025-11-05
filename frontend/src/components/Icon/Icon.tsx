import React from "react";

import IconWrapper from "@/components/Icon/styles/IconWrapper";

interface IconProps {
    id: string;
    size?: number;
    color?: string;
    opacity?: number;
    rotation?: number;
}

const Icon: React.FC<IconProps> = ({ id, size = 12, color, opacity, rotation }) => {
    const dynamicStyle: React.CSSProperties = {
        transform: `rotate(${rotation ?? 0}deg)`,
    };

    return (
        <IconWrapper $size={size} $color={color} $opacity={opacity} style={dynamicStyle}>
            <i className={id} />
        </IconWrapper>
    );
};

export default Icon;
