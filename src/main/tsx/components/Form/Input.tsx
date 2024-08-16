import React, {useEffect, useState} from "react";

import {motion} from "framer-motion";

import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import StyledInput from "@/components/Form/styles/StyledInput";
import StyledInputField from "@/components/Form/styles/StyledInputField";
import StyledInputFieldPlaceholder from "@/components/Form/styles/StyledInputFieldPlaceholder";
import StyledInputFieldWrapper from "@/components/Form/styles/StyledInputFieldWrapper";
import FieldErrorType from "@/components/Form/types/FieldErrorType";
import InputState from "@/components/Form/types/InputState";
import Icon from "@/components/Icon/Icon";

export interface InputProps<T> {
    property: string | number | symbol;
    inputFormatter: InputFormatter<T>;
    initial?: T | null;
    register?: (setValue: (value: T | null, errors: Array<FieldErrorType>) => void) => void;
    onChange?: (value: T | null, errors: Array<FieldErrorType>) => void;
    required?: boolean;
    textAlign?: 'left' | 'center' | 'right';
}

const Input = <T, >({
                        property,
                        inputFormatter,
                        initial = null,
                        register,
                        onChange,
                        required = false,
                        textAlign,
                    }: InputProps<T>) => {

    const [inputState, setInputState] = useState<InputState<T>>(inputFormatter.toInputState(initial));

    const [errors, setErrors] = useState(new Array<FieldErrorType>);

    // register so that useForm can use it
    useEffect(() => {
        if (register) {
            register((value: T | null, errors: Array<FieldErrorType>) => {
                setInputState(inputFormatter.toInputState(value));
                setErrors(errors);
            });
        }
    }, [register, inputFormatter]);

    const getErrors = (value: T | null) => {
        const errors: Array<FieldErrorType> = [];
        if (required && value === null) {
            errors.push(FieldErrorType.REQUIRED);
        }
        return errors;
    };

    return (
        <StyledInput>
            <StyledInputFieldWrapper>
                <StyledInputField
                    $textAlign={textAlign}
                    name={String(property)}
                    type="text"
                    onFocus={() => {
                        const state: InputState<T> = inputFormatter.onFocus(inputState);
                        setInputState(state);
                    }}
                    onChange={(event) => {
                        const value: string = event.target.value;
                        const state: InputState<T> = inputFormatter.onChange(inputState, value);
                        setInputState(state);
                        setErrors([]);
                    }}
                    onBlur={() => {
                        const value: T | null = inputFormatter.onBlur(inputState);
                        const currentErrors: Array<FieldErrorType> = getErrors(value);
                        setErrors(currentErrors);
                        onChange && onChange(value, currentErrors);
                    }}
                    value={inputState.value}
                />
                {inputState.prediction && (
                    <StyledInputFieldPlaceholder
                        as={motion.div}
                        initial={{opacity: 0}}
                        animate={{opacity: .5}}
                        exit={{opacity: 0}}
                        transition={{duration: .3}}
                    >
                        {inputState.prediction.label}
                    </StyledInputFieldPlaceholder>
                )}
            </StyledInputFieldWrapper>
            {errors.length > 0 && (
                <Icon id="fa-regular fa-circle-exclamation" size={18} color="red"/>
            )}
        </StyledInput>
    );
};

export default Input;
