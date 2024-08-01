import styled from "styled-components";

const StyledSidebarDummy = styled.div`
    grid-area: sidebar;
    width: 200px;
    border-right: ${props => `${props.theme.border.width}px solid ${props.theme.border.color}`};
`;

export default StyledSidebarDummy;
