import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/types/InputState";

/**
 * Input formatter for normal strings.
 */
class StringInputFormatter extends InputFormatter<string> {

    /**
     * @inheritDoc
     */
    toInputState = (value: string | null): InputState<string> => ({value: value ?? ''});

    /**
     * @inheritDoc
     */
    onFocus = (state: InputState<string>): InputState<string> => state;

    /**
     * @inheritDoc
     */
    onChange = (before: InputState<string>, after: string): InputState<string> => ({value: after});

    /**
     * @inheritDoc
     */
    onBlur = (state: InputState<string>): string | null => state.value;

}

export default StringInputFormatter;
