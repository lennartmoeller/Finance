import styled from "styled-components";

const StyledSidebar = styled.div`
    grid-area: sidebar;
    width: 170px;
    border-right: ${(props) => `${props.theme.border.width}px solid ${props.theme.border.color}`};
`;

export default StyledSidebar;
