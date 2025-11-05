export const measureTextWidth = (text: string, referenceElement: HTMLElement): number => {
    const tempSpan = document.createElement("span");
    tempSpan.style.visibility = "hidden";
    tempSpan.style.position = "absolute";
    tempSpan.style.whiteSpace = "pre";

    const computedStyle = globalThis.getComputedStyle(referenceElement);
    tempSpan.style.font = computedStyle.font;
    tempSpan.style.fontSize = computedStyle.fontSize;
    tempSpan.style.fontFamily = computedStyle.fontFamily;
    tempSpan.style.fontWeight = computedStyle.fontWeight;
    tempSpan.style.letterSpacing = computedStyle.letterSpacing;

    tempSpan.textContent = text;
    document.body.appendChild(tempSpan);
    const width = tempSpan.offsetWidth;
    tempSpan.remove();

    return width;
};
