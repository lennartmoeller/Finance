import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/InputFormatter/InputState";

/**
 * Input formatter for normal strings.
 */
class StringInputFormatter extends InputFormatter<string> {

    /**
     * @inheritDoc
     */
    toInputState = (value: string): InputState => ({value: value});

    /**
     * @inheritDoc
     */
    onChange = (before: InputState, after: string): InputState => ({value: after});

    /**
     * @inheritDoc
     */
    onBlur = (state: InputState): string => state.value;

}

export default StringInputFormatter;
