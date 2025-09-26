import styled from "styled-components";

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

export default StyledSkeleton;
