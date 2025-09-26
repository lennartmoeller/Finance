import styled from "styled-components";

const StyledTrackingView = styled.div`
    width: max-content;
    height: 100%;
    padding: ${(props) => props.theme.mainPadding}px;
    padding-top: 0;
    display: grid;
    grid-template:
        "transactionsTableFilters empty"
        "transactionsTable accountList";
    grid-template-rows: max-content 1fr;
    grid-auto-columns: max-content;
    gap: ${(props) => `${props.theme.mainPadding}px`};
`;

export default StyledTrackingView;
