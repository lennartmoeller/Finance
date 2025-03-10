import styled from "styled-components";

const StyledPerformanceArrow = styled.div.attrs<{
    $color?: string;
}>(({$color}) => ({
    style: {
        backgroundColor: $color,
    }
}))`
    border-radius: 50%;
    padding: 2px;
`;

export default StyledPerformanceArrow;
