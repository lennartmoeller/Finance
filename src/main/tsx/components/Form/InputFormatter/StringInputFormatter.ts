import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/InputFormatter/InputState";

/**
 * Input formatter for normal strings.
 */
class StringInputFormatter extends InputFormatter<string> {

    /**
     * @inheritDoc
     */
    toInputState = (value: string | null): InputState => ({value: value ?? ''});

    /**
     * @inheritDoc
     */
    onChange = (before: InputState, after: string): InputState => ({value: after});

    /**
     * @inheritDoc
     */
    onBlur = (state: InputState): string | null => state.value;

}

export default StringInputFormatter;
