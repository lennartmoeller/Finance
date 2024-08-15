import {createContext} from 'react';

interface FormContextType<V extends object, K extends keyof V> {
    initialValue: V | null;
    registerValueSetter: (property: K, setInputValue: (value: V[K] | null) => void) => void;
    updatePropertyValue: (changedProperty: K, changedValue: V[K] | null) => boolean;
}

const FormContext = createContext<FormContextType<object, keyof object> | undefined>(undefined);

export default FormContext;
