import FieldErrorType from "@/components/Form/types/FieldErrorType";

interface InputState<T> {
    value: string;
    errors: Array<FieldErrorType>;
    prediction?: { label: string; value: T | null };
}

export default InputState;
