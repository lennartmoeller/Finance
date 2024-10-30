import React from "react";

import TotalDiagram from "@/views/DiagramView/TotalDiagram";
import StyledDiagramContainer from "@/views/DiagramView/styles/StyledDiagramContainer";

const DiagramView: React.FC = () => {
    return (
        <StyledDiagramContainer>
            <TotalDiagram/>
        </StyledDiagramContainer>
    );
};

export default DiagramView;
