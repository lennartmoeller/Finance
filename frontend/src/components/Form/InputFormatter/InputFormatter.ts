import FieldErrorType from "@/components/Form/types/FieldErrorType";
import InputState from "@/components/Form/types/InputState";

export interface InputFormatterOptions {
    required?: boolean;
}

abstract class InputFormatter<V> {
    private readonly required: boolean;

    constructor(options: InputFormatterOptions = {}) {
        this.required = options.required ?? false;
    }

    public abstract valueToString(value: V | null): string;

    public abstract stringToValue(string: string): V | null;

    public validate(value: V | null): Array<FieldErrorType> {
        const result: Array<FieldErrorType> = [];
        if (this.required && value === null) {
            result.push(FieldErrorType.REQUIRED);
        }
        return result;
    }

    public valueToInputState(value: V | null): InputState<V> {
        return {
            errors: [], // no errors on initial render
            value: this.valueToString(value),
        };
    }

    public onFocus(state: InputState<V>): InputState<V> {
        return {
            errors: state.errors, // persist errors because nothing changed
            prediction: undefined, // no prediction on focus
            value: state.value,
        };
    }

    public onChange(before: InputState<V>, after: string): InputState<V> {
        return {
            errors: [], // reset errors on change
            prediction: undefined, // prediction will be calculated by InputFormatter implementation
            value: after,
        };
    }

    public onBlur(state: InputState<V>): InputState<V> {
        const value: V | null =
            state.prediction?.value ?? this.stringToValue(state.value);
        return {
            errors: this.validate(value), // validate on blur
            prediction: undefined, // no prediction on blur
            value: this.valueToString(value),
        };
    }
}

export default InputFormatter;
