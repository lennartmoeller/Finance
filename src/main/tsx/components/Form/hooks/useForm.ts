import {useRef} from "react";

import {InputProps} from "@/components/Form/Input";
import FieldErrorType from "@/components/Form/types/FieldErrorType";
import {Nullable} from "@/utils/types";

interface FormOptions<I extends object> {
    initial: Nullable<I>;
    onSuccess?: (item: Nullable<I>) => Nullable<I> | void;
    onError?: (item: Nullable<I>, errors: ItemErrors<I>) => Nullable<I> | void;
    onSettled?: (item: Nullable<I>, errors: ItemErrors<I>) => Nullable<I> | void;
}

export interface FormFieldState<T> {
    value: T | null;
    setValue: (value: T | null) => void;
    errors: Array<FieldErrorType>;
    hasFocus: boolean;
}

type ItemErrors<I> = Array<{ property: keyof I, errors: Array<FieldErrorType> }>;

type RegisterFunction<I> = (<P extends keyof I>(property: P) => Pick<InputProps<I[P]>, 'property' | 'initial' | 'register' | 'onChange'>);

const useForm = <I extends object>(formOptions: FormOptions<I>): RegisterFunction<I> => {

    const formFieldStateGetters = useRef(new Map<keyof I, () => FormFieldState<I[keyof I] | null>>);

    return <P extends keyof I>(property: P) => {

        const initial: Nullable<I>[P] = formOptions.initial[property];

        const register = (getFormFieldState: () => FormFieldState<I[P] | null>) => {
            formFieldStateGetters.current.set(
                property,
                getFormFieldState as () => FormFieldState<I[keyof I] | null>
            );
        };

        const onChange = (): void => {

            const formFieldStates: Map<keyof I, FormFieldState<I[keyof I] | null>> = new Map();
            formFieldStateGetters.current.forEach((getFormFieldState, property) => {
                formFieldStates.set(property, getFormFieldState());
            });

            // check if any form field has focus
            if (Array.from(formFieldStates.values()).some(({hasFocus}) => hasFocus)) {
                return;
            }

            // create item
            const item: Nullable<I> = formOptions.initial;
            formFieldStates.forEach(({value}, property) => {
                item[property] = value;
            });

            // collect errors
            const errors: ItemErrors<I> = [];
            formFieldStates.forEach(({errors: es}, p) => {
                if (es.length !== 0) {
                    errors.push({property: p, errors: es});
                }
            });

            // call onSuccess, onError, onSettled and update item
            let updatedItem: Nullable<I> = item;
            if (errors.length === 0 && formOptions.onSuccess) {
                const onSuccessResult = formOptions.onSuccess(updatedItem);
                if (onSuccessResult) {
                    updatedItem = onSuccessResult;
                }
            }
            if (errors.length > 0 && formOptions.onError) {
                const onErrorResult = formOptions.onError(updatedItem, errors);
                if (onErrorResult) {
                    updatedItem = onErrorResult;
                }
            }
            if (formOptions.onSettled) {
                const onSettledResult = formOptions.onSettled(updatedItem, errors);
                if (onSettledResult) {
                    updatedItem = onSettledResult;
                }
            }

            // update all input fields
            formFieldStates.forEach(({setValue}, property) => {
                setValue(updatedItem[property]);
            });

        };

        return {property, initial, register, onChange};

    };
};

export default useForm;
