import styled from "styled-components";

import withHiddenScrollbar from "@/styles/withHiddenScrollbar";

const StyledMainArea = withHiddenScrollbar(styled.div`
    grid-area: main;
    width: 100%;
    height: 100%;
    overflow-x: scroll;
    overflow-y: scroll;
`);

export default StyledMainArea;
