import styled from "styled-components";

const StyledAccountItem = styled.div`
    display: grid;
    grid-auto-flow: column;
    grid-auto-columns: max-content;
    place-content: space-between;
    align-content: center;
    align-items: center;
    width: 300px;
    padding: 15px 20px;
    border: ${props => `${props.theme.border.width}px solid ${props.theme.border.color}`};
`;

export default StyledAccountItem;
