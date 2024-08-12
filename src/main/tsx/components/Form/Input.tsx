import React, {useContext, useEffect, useState} from "react";

import FormContext from "@/components/Form/context/FormContext";
import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/InputFormatter/InputState";
import StyledInput from "@/components/Form/styles/StyledInput";
import StyledInputWrapper from "@/components/Form/styles/StyledInputWrapper";
import StyledPlaceholder from "@/components/Form/styles/StyledPlaceholder";

interface InputProps<T> {
    property: string,
    inputFormatter: InputFormatter<T>
    textAlign?: 'left' | 'center' | 'right',
}

const Input = <T, >({property, inputFormatter, textAlign,}: InputProps<T>) => {
    const formContext = useContext(FormContext);
    if (!formContext) {
        throw new Error("Input must be used within a Form");
    }

    const [inputValue, setInputValue] = useState<InputState>({value: ''});

    useEffect(() => {
        // @ts-ignore Context has no generic typing
        formContext.registerValueSetter(property, (value: T) => setInputValue(inputFormatter.toInputState(value)));
    }, [formContext, property, inputFormatter]);

    const onChange: React.ChangeEventHandler<HTMLInputElement> = (a: React.ChangeEvent<HTMLInputElement>): void => {
        const newValue: string = a.target.value;
        const newState: InputState = inputFormatter.onChange(inputValue, newValue);
        setInputValue(newState);
    };

    const onBlur: React.FocusEventHandler<HTMLInputElement> = () => {
        const convertedValue: T = inputFormatter.onBlur(inputValue);
        // @ts-ignore Context has no generic typing
        const changes: boolean = formContext.updatePropertyValue(property, convertedValue);
        if (!changes) {
            // input value formatting could be different
            setInputValue(inputFormatter.toInputState(convertedValue));
        }
    };

    return (
        <StyledInputWrapper>
            <StyledInput
                $textAlign={textAlign}
                type="text"
                onChange={onChange}
                onBlur={onBlur}
                value={inputValue.value}
            />
            <StyledPlaceholder>
                {inputValue.prediction}
            </StyledPlaceholder>
        </StyledInputWrapper>
    );


};

export default Input;
