import React, {ReactNode} from "react";

import styled from "styled-components";

import {withHiddenScrollbar} from "@/styles/withHiddenScrollbar";

const StyledSkeleton = styled.div`
    display: grid;
    overflow: hidden;
    width: 100vw;
    height: 100vh;
    grid-template:
    "sidebar header"
    "sidebar main";
    grid-template-columns: min-content 1fr;
    grid-template-rows: min-content 1fr;
`;

const StyledSidebarDummy = styled.div`
    grid-area: sidebar;
    width: 200px;
    border-right: ${props => `${props.theme.border.width}px solid ${props.theme.border.color}`};
`;

const StyledHeaderDummy = styled.div`
    grid-area: header;
    height: 100px;
`;

const StyledMainArea = withHiddenScrollbar(styled.div`
    grid-area: main;
    width: 100%;
    height: 100%;
    overflow-x: scroll;
    overflow-y: scroll;
`);

const StyledMainAreaInner = styled.div`
    width: max-content;
`;

interface SkeletonProps {
    children: ReactNode;
}

const Skeleton: React.FC<SkeletonProps> = ({children}) => {
    return <StyledSkeleton>
        <StyledSidebarDummy/>
        <StyledHeaderDummy/>
        <StyledMainArea>
            <StyledMainAreaInner>
                {children}
            </StyledMainAreaInner>
        </StyledMainArea>
    </StyledSkeleton>;
};

export default Skeleton;
