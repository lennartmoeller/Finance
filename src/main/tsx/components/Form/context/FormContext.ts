import {createContext} from 'react';

interface FormContextType<V extends object, K extends keyof V> {
    initialValue: V;
    registerValueSetter: (property: K, setInputValue: (value: V[K]) => void) => void;
    updatePropertyValue: (changedProperty: K, changedValue: V[K]) => boolean;
}

const FormContext = createContext<FormContextType<object, keyof object> | undefined>(undefined);

export default FormContext;
