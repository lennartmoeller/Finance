import styled from "styled-components";

const StyledSidebarLogoContainer = styled.div`
    display: grid;
    place-items: center;
    height: ${(props) => props.theme.header.height}px;
    padding: 15px;
    svg {
        height: 100%;
    }
`;

export default StyledSidebarLogoContainer;
