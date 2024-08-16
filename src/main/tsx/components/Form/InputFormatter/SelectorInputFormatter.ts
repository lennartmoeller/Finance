import Fuse from 'fuse.js';

import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/types/InputState";

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

    constructor(options: Array<T>, idProperty: K, labelProperty: L) {
        super();
        this.options = options;
        this.idProperty = idProperty;
        this.labelProperty = labelProperty;
        this.fuse = new Fuse(
            this.options,
            {
                keys: [this.labelProperty],
                includeScore: true,
            }
        );
    }

    /**
     * @inheritDoc
     */
    toInputState = (id: T[K] | null): InputState<T[K]> => {
        const option: T | undefined = this.options.find(option => option[this.idProperty] === id);
        return {value: option ? option[this.labelProperty] : ''};
    };

    /**
     * @inheritDoc
     */
    onFocus = (state: InputState<T[K]>): InputState<T[K]> => state;

    /**
     * @inheritDoc
     */
    onChange = (_before: InputState<T[K]>, after: string): InputState<T[K]> => {
        const prediction = this.getPrediction(after);

        if (prediction) {
            return {
                value: prediction.label.slice(0, after.length),
                prediction: prediction,
            };
        }

        return {value: after};
    };

    /**
     * @inheritDoc
     */
    onBlur = (state: InputState<T[K]>): T[K] | null => {
        const prediction = this.getPrediction(state.value);
        return prediction?.value ?? null;
    };

    private getPrediction = (search: string): { label: string, value: T[K] } | undefined => {
        const prediction = this.fuse.search(search).find(result => {
            if (result.score! > 0.5) {
                return false;
            }
            const targetString: string = result.item[this.labelProperty];
            const targetLower: string = targetString.toLowerCase();
            const searchLower: string = search.toLowerCase();
            return targetLower.startsWith(searchLower);
        });

        if (!prediction) {
            return undefined;
        }
        return {
            label: prediction.item[this.labelProperty],
            value: prediction.item[this.idProperty],
        };
    };
}

export default SelectorInputFormatter;
