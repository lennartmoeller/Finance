interface InputState<T> {
    value: string;
    prediction?: { label: string, value: T | null };
}

export default InputState;
