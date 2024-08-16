import {useRef} from "react";

import {InputProps} from "@/components/Form/Input";
import FieldErrorType from "@/components/Form/types/FieldErrorType";
import {Nullable} from "@/utils/types";

type ItemErrors<I> = Array<{ property: keyof I, errors: Array<FieldErrorType> }>;

interface FormOptions<I extends object> {
    initial: Nullable<I>;
    onChange: (formState: FormState<I>) => FormState<I> | void;
}

interface FormState<I> {
    item: Nullable<I>;
    errors: ItemErrors<I>;
}

type RegisterFunction<I> = (<P extends keyof I>(property: P) => Pick<InputProps<I[P]>, 'property' | 'initial' | 'register' | 'onChange'>);

const useForm = <I extends object>(formOptions: FormOptions<I>): RegisterFunction<I> => {

    const formState = useRef<FormState<I>>({item: formOptions.initial, errors: []});

    const setters = useRef(new Map<keyof I, (value: I[keyof I] | null, errors?: Array<FieldErrorType>) => void>);

    return <P extends keyof I>(property: P) => {

        const initial: Nullable<I>[P] = formOptions.initial[property];

        const register = (setValue: (value: I[P] | null, errors: Array<FieldErrorType>) => void) => {
            setters.current.set(
                property,
                setValue as ((value: I[keyof I] | null, errors?: Array<FieldErrorType>) => void)
            );
        };

        const onChange = (value: I[P] | null, fieldErrors: Array<FieldErrorType>): void => {
            const item: Nullable<I> = {...formState.current.item, [property]: value};
            const errors: ItemErrors<I> = formState.current.errors.filter(({property: p}) => p !== property);
            if (fieldErrors.length > 0) {
                errors.push({property, errors: fieldErrors});
            }
            const updatedFormState: FormState<I> = {item, errors};

            // call onChange to get the updated formState
            formState.current = formOptions.onChange(updatedFormState) ?? updatedFormState;

            // update all input fields
            setters.current.forEach((setter, property) => {
                const value = formState.current.item[property];
                const errors = formState.current.errors.find(({property: p}) => p === property)?.errors ?? [];
                return setter(value, errors);
            });
        };

        return {property, initial, register, onChange};

    };
};

export default useForm;
