import React, {useEffect} from "react";

import useHeader from "@/skeleton/Header/stores/useHeader";
import TotalDiagram from "@/views/DashboardView/Diagrams/TotalDiagram";
import StyledDiagramContainer from "@/views/DashboardView/styles/StyledDiagramContainer";

const DashboardView: React.FC = () => {
    const {setHeader} = useHeader();

    useEffect(() => {
        setHeader({});
    }, [setHeader]);

    return (
        <StyledDiagramContainer>
            <TotalDiagram/>
        </StyledDiagramContainer>
    );
};

export default DashboardView;
