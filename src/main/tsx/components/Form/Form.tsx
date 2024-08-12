import React, {ReactNode, useRef} from 'react';

import FormContext from "@/components/Form/context/FormContext";

interface FormProps<V extends object> {
    initial: V;
    onChange: (item: V) => V;
    children: ReactNode;
}

const TableRow = <V extends object>({initial, onChange, children}: FormProps<V>) => {
    const item = useRef(initial);

    const setters = useRef(new Map<keyof V, (value: V[keyof V]) => void>());

    const registerValueSetter = <K extends keyof V>(property: K, setValue: (value: V[K]) => void): void => {
        if (!setters.current.has(property)) {
            setters.current.set(property, setValue as (value: V[keyof V]) => void);
            setValue(initial[property]);
        }
    };

    const updatePropertyValue = <K extends keyof V>(changedProperty: K, changedValue: V[K]): boolean => {
        if (item.current[changedProperty] === changedValue) {
            return false; // do nothing: value hasn't changed
        }
        const updatedItem: V = onChange({...item.current, [changedProperty]: changedValue});
        for (const property in updatedItem) {
            if (item.current[property] === updatedItem[property]) {
                // do nothing: value hasn't changed
                continue;
            }
            const setter = setters.current.get(property);
            if (setter) {
                setter(updatedItem[property]);
            }
        }
        item.current = updatedItem;
        return true;
    };

    return (
        <FormContext.Provider value={{registerValueSetter, updatePropertyValue, initialValue: initial}}>
            {children}
        </FormContext.Provider>
    );
};

export default TableRow;
