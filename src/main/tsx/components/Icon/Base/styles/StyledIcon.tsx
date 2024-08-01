import styled from "styled-components";

const StyledIcon = styled.div.attrs<{
    $color?: string;
    $opacity?: number;
    $rotation?: number;
    $size: number;
}>(({$rotation}) => ({
    style: {
        transform: `rotate(${$rotation ?? 0}deg)`,
    }
}))`
    display: grid;
    place-content: center;
    width: ${({$size}) => $size}px;
    height: ${({$size}) => $size}px;
    opacity: ${({$opacity}) => $opacity ?? 1};

    & > svg {
        height: 100%;
        fill: ${({$color}) => $color};
    }
`;

export default StyledIcon;
