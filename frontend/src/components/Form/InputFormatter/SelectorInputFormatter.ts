import BaseSelectorInputFormatter, {
    SelectorInputFormatterOptions,
} from "@/components/Form/InputFormatter/BaseSelectorInputFormatter";
import InputState from "@/components/Form/types/InputState";

export { SelectorInputFormatterOptions };

class SelectorInputFormatter<
    T extends Record<K, number> & Record<L, string>,
    K extends keyof T & string,
    L extends keyof T & string,
> extends BaseSelectorInputFormatter<T, K, L, T[K]> {
    public valueToString(value: T[K] | null): string {
        const option: T | undefined = this.options.find(
            (option) => option[this.idProperty] === value,
        );
        return option ? option[this.labelProperty] : "";
    }

    public stringToValue(string: string): T[K] | null {
        const prediction: T | null = this.getPrediction(string);

        if (!prediction) {
            return null;
        }

        return prediction[this.idProperty];
    }

    public onChange(before: InputState<T[K]>, after: string): InputState<T[K]> {
        const prediction: T | null = this.getPrediction(after);

        const result: InputState<T[K]> = super.onChange(before, after);

        if (prediction) {
            return {
                ...result,
                value: (prediction[this.labelProperty] as string).slice(
                    0,
                    after.length,
                ),
                prediction: {
                    label: prediction[this.labelProperty],
                    value: prediction[this.idProperty],
                },
            };
        }

        return result;
    }

    public findPrediction(search: string): T | null {
        return this.getPrediction(search);
    }
}

export default SelectorInputFormatter;
