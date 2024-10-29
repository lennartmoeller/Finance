import React from "react";

import Input, {InputProps} from "@/components/Form/Input";
import StyledInputField from "@/components/InputField/styles/StyledInputField";
import StyledInputFieldContainer from "@/components/InputField/styles/StyledInputFieldContainer";
import StyledInputFieldLabel from "@/components/InputField/styles/StyledInputFieldLabel";

type InputFieldProps<T> = InputProps<T> & {
    label?: string;
    width?: number;
}

const InputField = <T, >({label, width, ...inputProps}: InputFieldProps<T>) => {
    return (
        <StyledInputFieldContainer>
            {label && (
                <StyledInputFieldLabel>
                    {label}
                </StyledInputFieldLabel>
            )}
            <StyledInputField $width={width}>
                <Input {...inputProps}/>
            </StyledInputField>
        </StyledInputFieldContainer>
    );
};

export default InputField;
