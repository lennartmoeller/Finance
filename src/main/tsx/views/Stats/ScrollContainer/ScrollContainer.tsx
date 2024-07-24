import React, {ReactNode, useCallback, useEffect, useRef} from "react";

import styled from "styled-components";

interface ScrollContainerProps {
    children: ReactNode;
    onScroll?: (scrollPercentage: number) => void;
}

const StyledScrollContainer = styled.div`
    width: 100%;
    overflow: scroll;
`;

const ScrollContainer: React.FC<ScrollContainerProps> = ({children, onScroll}) => {
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

    return <StyledScrollContainer ref={containerRef}>
        {children}
    </StyledScrollContainer>;
};

export default ScrollContainer;
