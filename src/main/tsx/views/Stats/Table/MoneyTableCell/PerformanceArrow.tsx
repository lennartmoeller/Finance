import React from "react";

import styled from "styled-components";
import Icon from "@/components/Icon/Icon";

interface PerformanceArrowProps {
    performance: number | undefined;
}

const StyledPerformanceArrow = styled.div.attrs<{
    $color?: string;
}>(({$color}) => ({
    style: {
        backgroundColor: $color,
    }
}))`
    border-radius: 50%;
    padding: 2px;
`;

const RED: string = 'FC5758';
const YELLOW: string = 'EECD19';
const GREEN: string = '4BB543';

const interpolateColor = (firstHex: string, secondHex: string, percent: number): string => {
    if (!/^#?[0-9A-Fa-f]{6}$/.test(firstHex) || !/^#?[0-9A-Fa-f]{6}$/.test(secondHex) || percent < 0 || percent > 1) {
        throw new Error('Invalid input');
    }
    const hexToRgb = (hex: string) => hex.replace('#', '').match(/.{1,2}/g)!.map(oct => parseInt(oct, 16));
    const firstRgb = hexToRgb(firstHex);
    const secondRgb = hexToRgb(secondHex);
    const interpolatedRgb = firstRgb.map((v, i) => Math.round(v * (1 - percent) + secondRgb[i] * percent));
    return '#' + interpolatedRgb.map(v => v.toString(16).padStart(2, '0')).join('');
};

const getArrowColor = (performance: number): string => {
    if (0 <= performance && performance <= 0.5) {
        return interpolateColor(RED, YELLOW, performance * 2);
    } else if (0.5 <= performance && performance <= 1) {
        return interpolateColor(YELLOW, GREEN, (performance - 0.5) * 2);
    } else {
        throw new Error('Invalid performance value');
    }
};

const PerformanceArrow: React.FC<PerformanceArrowProps> = ({performance}) => {
    return (
        <StyledPerformanceArrow $color={performance !== undefined ? getArrowColor(performance) : undefined}>
            <Icon
                id="fa-solid fa-arrow-down"
                color="white"
                opacity={performance !== undefined ? undefined : 0}
                rotation={performance !== undefined ? performance * -180 : undefined}
                size={10}/>
        </StyledPerformanceArrow>
    );
};

export default PerformanceArrow;
