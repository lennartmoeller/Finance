import {useRef} from "react";

import {InputProps} from "@/components/Form/Input";
import {Nullable} from "@/utils/types";

interface FormOptions<I extends object> {
    initial: Nullable<I>;
    onChange: (item: Nullable<I>) => Nullable<I>;
}

type RegisterFunction<I> = (<P extends keyof I>(property: P) => Pick<InputProps<I[P]>, 'initial' | 'register' | 'onChange'>);

const useForm = <I extends object>(options: FormOptions<I>): RegisterFunction<I> => {
    const item = useRef(options.initial);

    const setters = useRef(new Map<keyof I, (value: I[keyof I] | null) => void>());

    return <P extends keyof I>(property: P) => {
        return {
            initial: Object.hasOwn(options.initial, property) ? options.initial[property] : null,
            register: (setValue: (value: I[P] | null) => void) => setters.current.set(property, setValue as (value: I[keyof I] | null) => void),
            onChange: (value: I[P] | null): void => {
                if (item.current[property] === value) {
                    return; // do nothing: value hasn't changed
                }
                const updatedItem: Nullable<I> = options.onChange({...item.current, [property]: value});
                for (const prop in updatedItem) {
                    if (item.current[prop] === updatedItem[prop]) {
                        continue; // do nothing: value hasn't changed
                    }
                    const setter = setters.current.get(prop);
                    if (setter) {
                        setter(updatedItem[prop]);
                    }
                }
                item.current = updatedItem;
            },
        };
    };
};

export default useForm;
