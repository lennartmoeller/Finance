import styled from "styled-components";

import StyledCell from "@/components/Table/styles/StyledCell";

const StyledHeaderCell = styled(StyledCell).attrs({as: 'th'})`
    background-color: #f8f8f8;
`;

export default StyledHeaderCell;
