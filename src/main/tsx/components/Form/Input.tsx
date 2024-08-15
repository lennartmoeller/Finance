import React, {useContext, useEffect, useState} from "react";

import FormContext from "@/components/Form/context/FormContext";
import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/InputFormatter/InputState";
import StyledInput from "@/components/Form/styles/StyledInput";
import StyledInputWrapper from "@/components/Form/styles/StyledInputWrapper";
import StyledPlaceholder from "@/components/Form/styles/StyledPlaceholder";

interface InputProps<I extends object, P extends keyof I> {
    property: P,
    inputFormatter: InputFormatter<I[P]>
    textAlign?: 'left' | 'center' | 'right',
}

const Input = <I extends object, P extends keyof I>(
    {
        property,
        inputFormatter,
        textAlign,
    }: InputProps<I, P>) => {
    const formContext = useContext(FormContext);
    if (!formContext) {
        throw new Error("Input must be used within a Form");
    }

    const [inputState, setInputState] = useState<InputState>({value: ''});

    useEffect(() => {
        // @ts-ignore Context has no generic typing
        formContext.registerValueSetter(property, (value: I[P] | null) => setInputState(inputFormatter.toInputState(value)));
    }, [formContext, property, inputFormatter]);

    const onChange: React.ChangeEventHandler<HTMLInputElement> = (a: React.ChangeEvent<HTMLInputElement>): void => {
        const newValue: string = a.target.value;
        const newState: InputState = inputFormatter.onChange(inputState, newValue);
        setInputState(newState);
    };

    const onBlur: React.FocusEventHandler<HTMLInputElement> = () => {
        const convertedValue: I[P] | null = inputFormatter.onBlur(inputState);
        // @ts-ignore Context has no generic typing
        const changes: boolean = formContext.updatePropertyValue(property, convertedValue);
        if (!changes) {
            // input value formatting could be different
            setInputState(inputFormatter.toInputState(convertedValue));
        }
    };

    return (
        <StyledInputWrapper>
            <StyledInput
                $textAlign={textAlign}
                type="text"
                onChange={onChange}
                onBlur={onBlur}
                value={inputState.value}
            />
            <StyledPlaceholder>
                {inputState.prediction}
            </StyledPlaceholder>
        </StyledInputWrapper>
    );


};

export default Input;
