import Fuse from "fuse.js";

import InputFormatter, {
    InputFormatterOptions,
} from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/types/InputState";

interface SelectorInputFormatterOptions<T, K, L> extends InputFormatterOptions {
    options: Array<T>;
    idProperty: K;
    labelProperty: L;
}

/**
 * Input formatter for cent values.
 */
class SelectorInputFormatter<
    T extends Record<K, number> & Record<L, string>,
    K extends keyof T & string,
    L extends keyof T & string,
> extends InputFormatter<T[K]> {
    private readonly options: Array<T>;
    private readonly idProperty: K;
    private readonly labelProperty: L;
    private readonly fuse: Fuse<T>;

    constructor(options: SelectorInputFormatterOptions<T, K, L>) {
        super(options);
        this.options = options.options;
        this.idProperty = options.idProperty;
        this.labelProperty = options.labelProperty;
        this.fuse = new Fuse(this.options, {
            keys: [this.labelProperty],
            includeScore: true,
        });
    }

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

    /**
     * @inheritDoc
     */
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

    private getPrediction = (search: string): T | null => {
        const prediction = this.fuse.search(search).find((result) => {
            if (result.score! > 0.5) {
                return false;
            }
            const targetString: string = result.item[this.labelProperty];
            const targetLower: string = targetString.toLowerCase();
            const searchLower: string = search.toLowerCase();
            return targetLower.startsWith(searchLower);
        });

        if (!prediction) {
            return null;
        }

        return prediction.item;
    };
}

export default SelectorInputFormatter;
