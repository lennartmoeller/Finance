const RED: string = "FC5758";
const YELLOW: string = "EECD19";
const GREEN: string = "4BB543";

const interpolateColor = (firstHex: string, secondHex: string, percent: number): string => {
    if (!/^#?[0-9A-Fa-f]{6}$/.test(firstHex) || !/^#?[0-9A-Fa-f]{6}$/.test(secondHex) || percent < 0 || percent > 1) {
        throw new Error("Invalid input");
    }
    const hexToRgb = (hex: string) =>
        hex
            .replace("#", "")
            .match(/.{1,2}/g)!
            .map((oct) => parseInt(oct, 16));
    const firstRgb = hexToRgb(firstHex);
    const secondRgb = hexToRgb(secondHex);
    const interpolatedRgb = firstRgb.map((v, i) => Math.round(v * (1 - percent) + secondRgb[i] * percent));
    return "#" + interpolatedRgb.map((v) => v.toString(16).padStart(2, "0")).join("");
};

const getPerformanceColor = (performance: number): string => {
    if (0 <= performance && performance <= 0.5) {
        return interpolateColor(RED, YELLOW, performance * 2);
    } else if (0.5 <= performance && performance <= 1) {
        return interpolateColor(YELLOW, GREEN, (performance - 0.5) * 2);
    } else {
        return `#${GREEN}`;
    }
};

export default getPerformanceColor;
