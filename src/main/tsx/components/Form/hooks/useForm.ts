import {useRef} from "react";

import isEqual from "react-fast-compare";

import {InputProps} from "@/components/Form/Input";
import FieldErrorType from "@/components/Form/types/FieldErrorType";
import {Nullable} from "@/utils/types";

interface FormOptions<I extends object> {
    initial: Nullable<I>;
    onSuccess?: (item: I) => Promise<Nullable<I> | void>;
    onError?: (item: Nullable<I>, errors: ItemErrors<I>) => Promise<Nullable<I> | void>;
    onSettled?: (item: Nullable<I>, errors: ItemErrors<I>) => Promise<Nullable<I> | void>;
    resetOnSuccess?: boolean;
}

export interface FormFieldState<T> {
    value: T | null;
    setValue: (value: T | null) => void;
    errors: Array<FieldErrorType>;
    hasFocus: boolean;
    reset: () => void;
}

type ItemErrors<I> = Array<{ property: keyof I, errors: Array<FieldErrorType> }>;

type RegisterFunction<I> = (<P extends keyof I>(property: P) => Pick<InputProps<I[P]>, 'property' | 'initial' | 'register' | 'onChange'>);

const useForm = <I extends object>(
    {
        initial,
        onSuccess,
        onError,
        onSettled,
        resetOnSuccess = false,
    }: FormOptions<I>
): RegisterFunction<I> => {

    const formFieldStateGetters = useRef(new Map<keyof I, () => FormFieldState<I[keyof I] | null>>);

    return <P extends keyof I>(property: P) => {

        const register = (getFormFieldState: () => FormFieldState<I[P] | null>) => {
            formFieldStateGetters.current.set(
                property,
                getFormFieldState as () => FormFieldState<I[keyof I] | null>
            );
        };

        const onChange = async (): Promise<void> => {

            const formFieldStates: Map<keyof I, FormFieldState<I[keyof I] | null>> = new Map();
            formFieldStateGetters.current.forEach((getFormFieldState, property) => {
                formFieldStates.set(property, getFormFieldState());
            });

            // check if any form field has focus
            if (Array.from(formFieldStates.values()).some(({hasFocus}) => hasFocus)) {
                return;
            }

            // create item
            const item: Nullable<I> = {...initial};
            formFieldStates.forEach(({value}, property) => {
                item[property] = value;
            });

            // check if item has changed
            if (isEqual(item, initial)) {
                return;
            }

            // collect errors
            const errors: ItemErrors<I> = [];
            formFieldStates.forEach(({errors: es}, p) => {
                if (es.length !== 0) {
                    errors.push({property: p, errors: es});
                }
            });

            // call onSuccess, onError, onSettled and update item
            let updatedItem: Nullable<I> = item;
            if (errors.length === 0 && onSuccess) {
                const onSuccessResult = await onSuccess(updatedItem as I);
                if (onSuccessResult) {
                    updatedItem = onSuccessResult;
                }
            }
            if (errors.length > 0 && onError) {
                const onErrorResult = await onError(updatedItem, errors);
                if (onErrorResult) {
                    updatedItem = onErrorResult;
                }
            }
            if (onSettled) {
                const onSettledResult = await onSettled(updatedItem, errors);
                if (onSettledResult) {
                    updatedItem = onSettledResult;
                }
            }

            // reset all input fields
            if (errors.length === 0 && resetOnSuccess) {
                formFieldStates.forEach(({reset}) => {
                    reset();
                });
                return;
            }

            // update all input fields
            formFieldStates.forEach(({setValue}, property) => {
                setValue(updatedItem[property]);
            });

        };

        return {
            initial: initial[property],
            onChange,
            property,
            register,
        };

    };
};

export default useForm;
