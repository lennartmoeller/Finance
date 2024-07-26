import React, { ReactNode, useEffect, useRef } from 'react';

import { arrow, offset, shift, Strategy, useFloating } from '@floating-ui/react';
import styled from 'styled-components';

const StyledTooltipContainer = styled.div<{ position: Strategy, top?: number, left?: number }>`
    position: ${(props) => props.position};
    top: ${(props) => (props.top ?? 0)}px;
    left: ${(props) => (props.left ?? 0)}px;
    z-index: 2;
    background: rgba(0, 0, 0, 0.75);
    padding: 5px 10px;
    border-radius: 4px;
    pointer-events: none;
    color: white;
    font-size: 12px;
`;

const StyledArrow = styled.div<{ width: number, height: number, opacity: number, x?: number, y?: number }>`
    position: absolute;
    top: ${(props) => ((props.y ?? 0) - props.height) + 'px'};
    left: ${(props) => props.x ? `${props.x}px` : '50%'};
    height: ${(props) => props.height}px;
    opacity: ${(props) => props.opacity};
    display: grid;
    place-items: center;
`;

interface TooltipProps {
    x: number;
    y: number;
    children: ReactNode;
}

const Tooltip: React.FC<TooltipProps> = ({ x, y, children }) => {
    const arrowRef = useRef<HTMLDivElement>(null);

    const { x: tooltipX, y: tooltipY, strategy, refs, update, middlewareData } = useFloating({
        placement: 'bottom',
        middleware: [
            shift({padding: 10}), // if tooltip is too close to the edge of the screen, it will be shifted
            offset(20), // spacing to the target element
            arrow({
                element: arrowRef,
                padding: 5, // to have a minimum of 5px between the arrow and the tooltip sides
            })],
    });

    useEffect(() => {
        update();
    }, [x, y, update]);

    const arrowHeight = 7;
    const arrowWidth = 10;
    const arrowOpacity = 0.75;

    return (
        <>
            <div ref={refs.setReference} style={{ position: 'absolute', left: x, top: y }} />
            <StyledTooltipContainer
                ref={refs.setFloating}
                position={strategy}
                top={tooltipY ?? 0}
                left={tooltipX ?? 0}
            >
                {children}
                <StyledArrow
                    ref={arrowRef}
                    x={middlewareData.arrow?.x}
                    y={middlewareData.arrow?.y}
                    height={arrowHeight}
                    width={arrowWidth}
                    opacity={arrowOpacity}
                >
                    <svg width={arrowWidth} height={arrowHeight} viewBox={`0 0 ${arrowWidth} ${arrowHeight}`} xmlns="http://www.w3.org/2000/svg">
                        <polygon points={`0, ${arrowHeight} ${arrowWidth}, ${arrowHeight} ${arrowWidth / 2}, 0`} fill="black" />
                    </svg>
                </StyledArrow>
            </StyledTooltipContainer>
        </>
    );
};

export default Tooltip;
