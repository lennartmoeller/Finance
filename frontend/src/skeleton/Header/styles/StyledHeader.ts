import styled from "styled-components";

const StyledHeader = styled.div`
    grid-area: header;
    height: ${(props) => props.theme.header.height}px;
    display: grid;
    grid-template-columns: max-content 1fr max-content;
    align-items: center;
    gap: 10px;
    padding: 0 ${(props) => props.theme.mainPadding + 5}px;
`;

export default StyledHeader;
