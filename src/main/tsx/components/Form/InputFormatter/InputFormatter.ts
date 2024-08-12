import InputState from "@/components/Form/InputFormatter/InputState";

/**
 * Interface for input formatters.
 * @template V - The type of the value.
 */
abstract class InputFormatter<V> {
    /**
     * Converts the value to an input state.
     * @param value - The value to convert.
     * @returns The input state.
     */
    abstract toInputState: (value: V) => InputState;

    /**
     * Corrects the input value direct after user input.
     * @param before - The input state before the change.
     * @param after - The input value after the change.
     * @returns The input state to apply.
     */
    abstract onChange: (before: InputState, after: string) => InputState;

    /**
     * Converts the input state to the final value.
     * @param value - The input state.
     * @returns The final value.
     */
    abstract onBlur: (value: InputState) => V;
}

export default InputFormatter;
