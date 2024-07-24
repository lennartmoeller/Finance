import React, {ReactNode, useRef, useEffect, useCallback} from "react";
import styled from "styled-components";

interface ScrollContainerProps {
    children: ReactNode;
    onScroll?: (scrollPercentage: number) => void;
    scrollPercentage?: number;
    disableScroll?: boolean; // New prop to disable manual scrolling
}

interface StyledContainerProps {
    disableScroll?: boolean;
}

const StyledScrollContainer = styled.div<StyledContainerProps>`
    width: 100%;
    overflow: ${({disableScroll}) => (disableScroll ? 'hidden' : 'scroll')};
`;

const ScrollContainer: React.FC<ScrollContainerProps> = ({children, onScroll, scrollPercentage, disableScroll}) => {
    const containerRef = useRef<HTMLDivElement>(null);

    const handleScroll = useCallback(() => {
        if (containerRef.current && onScroll) {
            const {scrollLeft, scrollWidth, clientWidth} = containerRef.current;
            const scrollPercentage = (scrollLeft / (scrollWidth - clientWidth)) * 100;
            onScroll(scrollPercentage);
        }
    }, [onScroll]);

    useEffect(() => {
        const container = containerRef.current;
        if (container) {
            container.addEventListener('scroll', handleScroll);
            return () => {
                container.removeEventListener('scroll', handleScroll);
            };
        }
    }, [handleScroll]);

    // Update scroll position when scrollPercentage prop changes
    useEffect(() => {
        const container = containerRef.current;
        if (container && scrollPercentage !== undefined) {
            const {scrollWidth, clientWidth} = container;
            const newScrollLeft = ((scrollWidth - clientWidth) * scrollPercentage) / 100;
            container.scrollLeft = newScrollLeft;
        }
    }, [scrollPercentage]);

    // Prevent manual scroll when disableScroll is true
    useEffect(() => {
        const container = containerRef.current;
        const preventScroll = (e: Event) => {
            if (disableScroll) {
                e.preventDefault();
            }
        };

        if (container) {
            container.addEventListener('wheel', preventScroll, {passive: false});
            container.addEventListener('touchmove', preventScroll, {passive: false});
            return () => {
                container.removeEventListener('wheel', preventScroll);
                container.removeEventListener('touchmove', preventScroll);
            };
        }
    }, [disableScroll]);

    return (
        <StyledScrollContainer ref={containerRef} disableScroll={disableScroll}>
            {children}
        </StyledScrollContainer>
    );
};

export default ScrollContainer;
