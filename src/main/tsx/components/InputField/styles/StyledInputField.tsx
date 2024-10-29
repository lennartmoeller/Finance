import styled from "styled-components";

const paddingHorizontal = 12;
const paddingVertical = 8;

const StyledInputField = styled.div<{
    $width?: number;
}>`
    border-width: ${({theme}) => theme.border.width}px;
    border-style: solid;
    border-color: ${({theme}) => theme.border.color};
    border-radius: ${({theme}) => theme.border.radius}px;
    padding: ${paddingVertical}px ${paddingHorizontal}px;
    width: ${({$width}) => $width}px;
    background-color: #f8f8f8;
    opacity: .85;
    transition: background-color .2s, opacity .2s;

    &:focus-within {
        background-color: white;
        opacity: 1;
        border-color: ${({theme}) => theme.colors.accent};
        border-width: ${({theme}) => theme.border.widthFocus}px;
        padding: ${({theme}) => {
            const borderIncrease: number = theme.border.widthFocus - theme.border.width;
            return `${paddingVertical - borderIncrease}px ${paddingHorizontal - borderIncrease}px`;
        }}
    }
`;

export default StyledInputField;
