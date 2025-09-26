import styled from "styled-components";

const StyledArrow = styled.div<{
    $width: number;
    $height: number;
    $opacity: number;
    $x?: number;
    $y?: number;
}>`
    position: absolute;
    top: ${({ $y, $height }) => ($y ?? 0) - $height + "px"};
    left: ${({ $x }) => ($x ? `${$x}px` : "50%")};
    height: ${({ $height }) => $height}px;
    opacity: ${({ $opacity }) => $opacity};
    display: grid;
    place-items: center;
`;

export default StyledArrow;
