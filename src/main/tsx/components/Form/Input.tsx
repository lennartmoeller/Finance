import React, {useState} from "react";

import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/InputFormatter/InputState";
import StyledInput from "@/components/Form/styles/StyledInput";
import StyledInputWrapper from "@/components/Form/styles/StyledInputWrapper";
import StyledPlaceholder from "@/components/Form/styles/StyledPlaceholder";

export interface InputProps<T> {
    initial: T | null;
    onChange: (value: T | null) => void;
    register?: (setValue: (value: T | null) => void) => void;
    inputFormatter: InputFormatter<T>
    textAlign?: 'left' | 'center' | 'right',
}

const Input = <T, >({initial, register, onChange, inputFormatter, textAlign,}: InputProps<T>) => {
    const [inputState, setInputState] = useState<InputState>(inputFormatter.toInputState(initial));

    if (register) {
        // register so that useForm can use them
        register(
            (value: T | null) => setInputState(inputFormatter.toInputState(value))
        );
    }

    return (
        <StyledInputWrapper>
            <StyledInput
                $textAlign={textAlign}
                type="text"
                onChange={(event) => {
                    const value: string = event.target.value;
                    const state: InputState = inputFormatter.onChange(inputState, value);
                    setInputState(state);
                }}
                onBlur={() => {
                    const value: T | null = inputFormatter.onBlur(inputState);
                    onChange(value);
                }}
                value={inputState.value}
            />
            <StyledPlaceholder>
                {inputState.prediction}
            </StyledPlaceholder>
        </StyledInputWrapper>
    );

};

export default Input;
