import React, {RefObject, useEffect, useRef, useState} from "react";

import {motion} from "framer-motion";

import {FormFieldState} from "@/components/Form/hooks/useForm";
import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import StyledInput from "@/components/Form/styles/StyledInput";
import StyledInputField from "@/components/Form/styles/StyledInputField";
import StyledInputFieldPlaceholder from "@/components/Form/styles/StyledInputFieldPlaceholder";
import StyledInputFieldWrapper from "@/components/Form/styles/StyledInputFieldWrapper";
import InputState from "@/components/Form/types/InputState";
import Icon from "@/components/Icon/Icon";

export interface InputProps<T> {
    property: string | number | symbol;
    inputFormatter: InputFormatter<T>;
    initial?: T | null;
    register?: (getFormFieldState: () => FormFieldState<T | null>) => void;
    onChange?: () => void;
    textAlign?: 'left' | 'center' | 'right';
}

const Input = <T, >(
    {
        property,
        inputFormatter,
        initial = null,
        register,
        onChange,
        textAlign,
    }: InputProps<T>
) => {
    const input: RefObject<HTMLInputElement> = useRef(null);

    const [inputState, setInputState] = useState<InputState<T>>(inputFormatter.valueToInputState(initial));

    // register once so that useForm can use it
    useEffect(() => {
        register?.(
            () => {
                if (!input.current) {
                    throw new Error('Input not registered');
                }
                const value: T | null = inputFormatter.stringToValue(input.current.value);
                return {
                    value: value,
                    setValue: (value: T | null) => {
                        setInputState((previous) => ({
                            ...previous,
                            value: inputFormatter.valueToString(value),
                        }));
                    },
                    errors: inputFormatter.validate(value),
                    hasFocus: document.activeElement === input.current,
                };
            }
        );
    });

    return (
        <StyledInput>
            <StyledInputFieldWrapper>
                <StyledInputField
                    ref={input}
                    name={String(property)}
                    value={inputState.value}
                    onFocus={() => {
                        setInputState((previous: InputState<T>) => inputFormatter.onFocus(previous));
                    }}
                    onChange={(event) => {
                        const stringValue: string = event.target.value;
                        const newInputState: InputState<T> = inputFormatter.onChange(inputState, stringValue);
                        setInputState(newInputState);
                    }}
                    onBlur={async () => {
                        setInputState((previous: InputState<T>) => inputFormatter.onBlur(previous));
                        await new Promise(requestAnimationFrame); // wait until new element is focused
                        onChange?.();
                    }}
                    type="text"
                    $textAlign={textAlign}
                />
                {inputState.prediction && (
                    <StyledInputFieldPlaceholder
                        as={motion.div}
                        initial={{opacity: 0}}
                        animate={{opacity: .5}}
                        exit={{opacity: 0}}
                        transition={{duration: .25}}
                    >
                        {inputState.prediction.label}
                    </StyledInputFieldPlaceholder>
                )}
            </StyledInputFieldWrapper>
            {inputState.errors.length > 0 && (
                <Icon id="fa-regular fa-circle-exclamation" size={18} color="red"/>
            )}
        </StyledInput>
    );
};

export default Input;
