import React, { useEffect, useMemo, useRef, useState } from "react";

import { motion } from "framer-motion";

import { FormFieldState } from "@/components/Form/hooks/useForm";
import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import StyledInput from "@/components/Form/styles/StyledInput";
import StyledInputField from "@/components/Form/styles/StyledInputField";
import StyledInputFieldPlaceholder from "@/components/Form/styles/StyledInputFieldPlaceholder";
import StyledInputFieldWrapper from "@/components/Form/styles/StyledInputFieldWrapper";
import InputState from "@/components/Form/types/InputState";
import Icon from "@/components/Icon/Icon";
import { measureTextWidth } from "@/utils/dom";

export interface InputProps<T> {
    property: string | number | symbol;
    inputFormatter: InputFormatter<T>;
    autoFocus?: boolean;
    initial?: T | null;
    onChange?: () => Promise<void>;
    register?: (getFormFieldState: () => FormFieldState<T | null>) => void;
    textAlign?: "left" | "center" | "right";
}

const Input = <T,>({
    property,
    inputFormatter,
    autoFocus = false,
    initial = null,
    onChange,
    register,
    textAlign,
}: InputProps<T>) => {
    const input = useRef<HTMLInputElement>(null);
    const [inputState, setInputState] = useState<InputState<T>>(inputFormatter.valueToInputState(initial));
    const [isRegistered, setIsRegistered] = useState(false);

    const completionPositioning = useMemo(() => {
        if (
            !inputState.prediction ||
            !input.current ||
            !inputState.prediction.label.toLowerCase().startsWith(inputState.value.toLowerCase())
        ) {
            return null;
        }

        const completion = inputState.prediction.label.slice(inputState.value.length);
        const typedWidth = measureTextWidth(inputState.value, input.current);
        const completionWidth = measureTextWidth(completion, input.current);
        const containerWidth = input.current.offsetWidth;

        const fitsInContainer = typedWidth + completionWidth <= containerWidth;

        return {
            completion,
            leftOffset: fitsInContainer ? typedWidth : undefined,
            reducedWidth: fitsInContainer ? undefined : completionWidth,
        };
    }, [inputState.prediction, inputState.value]);

    useEffect(() => {
        if (!input.current || isRegistered) {
            return;
        }
        setIsRegistered(true);
        register?.(() => {
            if (!input.current) {
                throw new Error("Input not registered");
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
                reset: () => {
                    setInputState(inputFormatter.valueToInputState(initial));
                    if (autoFocus) {
                        input.current!.focus();
                    }
                },
            };
        });
    }, [autoFocus, initial, inputFormatter, isRegistered, register]);

    return (
        <StyledInput>
            <StyledInputFieldWrapper>
                <StyledInputField
                    ref={input}
                    name={String(property)}
                    value={inputState.value}
                    $reducedWidth={completionPositioning?.reducedWidth}
                    onFocus={() => {
                        setInputState((previous: InputState<T>) => inputFormatter.onFocus(previous));
                    }}
                    onChange={(event) => {
                        const stringValue: string = event.target.value;
                        const newInputState: InputState<T> = inputFormatter.onChange(inputState, stringValue);
                        setInputState(newInputState);
                    }}
                    onKeyDown={async (event: React.KeyboardEvent<HTMLInputElement>) => {
                        if (event.key === "Enter") {
                            input.current?.blur();
                            await new Promise(requestAnimationFrame); // wait until new element is focused
                            input.current?.focus();
                        }
                    }}
                    onBlur={async () => {
                        setInputState((previous: InputState<T>) => inputFormatter.onBlur(previous));
                        await new Promise(requestAnimationFrame); // wait until new element is focused
                        await onChange?.();
                    }}
                    autoFocus={autoFocus}
                    type="text"
                    $textAlign={textAlign}
                />
                {completionPositioning && (
                    <StyledInputFieldPlaceholder
                        as={motion.div}
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 0.5 }}
                        exit={{ opacity: 0 }}
                        transition={{ duration: 0.25 }}
                        $leftOffset={completionPositioning.leftOffset}
                    >
                        {completionPositioning.completion}
                    </StyledInputFieldPlaceholder>
                )}
            </StyledInputFieldWrapper>
            {inputState.errors.length > 0 && <Icon id="fa-regular fa-circle-exclamation" size={18} color="red" />}
        </StyledInput>
    );
};

export default Input;
