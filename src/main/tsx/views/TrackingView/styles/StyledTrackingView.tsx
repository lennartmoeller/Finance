import styled from "styled-components";

const StyledTrackingView = styled.div`
    display: grid;
    overflow: hidden;
    height: 100%;
    grid-template:
    "yearMonthSelector empty"
    "transactionsTable accountList";
    grid-template-rows: max-content 1fr;
    grid-auto-columns: max-content;
    padding: ${props => `${props.theme.mainPadding}px`};
    grid-gap: ${props => `${props.theme.mainPadding}px`};
`;

export default StyledTrackingView;
