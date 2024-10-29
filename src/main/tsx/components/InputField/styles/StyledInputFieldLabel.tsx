import styled from "styled-components";

const StyledInputFieldLabel = styled.div`
    font-size: ${props => `${props.theme.fontSize - 1}px`};
    font-weight: ${({theme}) => theme.inputField.label.fontWeight};
    letter-spacing: ${({theme}) => theme.inputField.label.letterSpacing};
    padding: 6px 10px;
`;

export default StyledInputFieldLabel;
