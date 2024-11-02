import styled from "styled-components";

const StyledTrackingView = styled.div`
    display: grid;
    height: 100%;
    grid-template:
    "transactionsTableFilters empty"
    "transactionsTable accountList";
    grid-template-rows: max-content 1fr;
    grid-auto-columns: max-content;
    gap: ${props => `${props.theme.mainPadding}px`};
`;

export default StyledTrackingView;
