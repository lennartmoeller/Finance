import React, {ReactNode} from "react";

import StyledHeaderDummy from "@/components/Skeleton/styles/StyledHeaderDummy";
import StyledMainArea from "@/components/Skeleton/styles/StyledMainArea";
import StyledMainAreaInner from "@/components/Skeleton/styles/StyledMainAreaInner";
import StyledSidebarDummy from "@/components/Skeleton/styles/StyledSidebarDummy";
import StyledSkeleton from "@/components/Skeleton/styles/StyledSkeleton";

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
