import styled from "styled-components";

const IconWrapper = styled.div<{
    $size: number;
    $color?: string;
    $opacity?: number;
}>`
    display: grid;
    place-content: center;
    width: ${({ $size }) => `${$size}px`};
    height: ${({ $size }) => `${$size}px`};
    font-size: ${({ $size }) => `${$size}px`};
    color: ${({ $color }) => $color ?? "inherit"};
    opacity: ${({ $opacity }) => $opacity ?? 1};
`;

export default IconWrapper;
