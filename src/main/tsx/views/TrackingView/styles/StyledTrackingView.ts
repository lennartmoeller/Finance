import styled from "styled-components";

const StyledTrackingView = styled.div`
    display: grid;
    width: max-content;
    height: 100%;
    grid-template:
    "transactionsTableFilters empty"
    "transactionsTable accountList";
    grid-template-rows: max-content 1fr;
    grid-auto-columns: max-content;
    padding: ${props => `${props.theme.mainPadding}px`};
    gap: ${props => `${props.theme.mainPadding}px`};
`;

export default StyledTrackingView;
