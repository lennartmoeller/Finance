import { ComponentType } from "react";

import styled from "styled-components";

const withHiddenScrollbar = <P extends Record<string, unknown>>(Component: ComponentType<P>) => styled(Component)`
    ::-webkit-scrollbar {
        display: none;
    }

    scrollbar-width: none;
    -ms-overflow-style: none;
`;

export default withHiddenScrollbar;
