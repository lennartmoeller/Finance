import Fuse from 'fuse.js';

import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";
import InputState from "@/components/Form/InputFormatter/InputState";

/**
 * Input formatter for cent values.
 */
class SelectorInputFormatter<
    T extends Record<K, number> & Record<L, string>,
    K extends keyof T & string,
    L extends keyof T & string,
> extends InputFormatter<T[K] | null> {

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
    toInputState = (id: T[K] | null): InputState => {
        const option: T | undefined = this.options.find(option => option[this.idProperty] === id);
        return {value: option ? option[this.labelProperty] : ''};
    };

    /**
     * @inheritDoc
     */
    onChange = (_before: InputState, after: string): InputState => {
        const prediction = this.getPrediction(after);

        if (prediction) {
            return {
                value: prediction.slice(0, after.length),
                prediction: prediction,
            };
        }

        return {value: after};
    };

    /**
     * @inheritDoc
     */
    onBlur = (state: InputState): T[K] | null => {
        const prediction = this.getPrediction(state.value);
        if (prediction) {
            const option: T | undefined = this.options.find(option => option[this.labelProperty] === prediction);
            if (option) {
                return option[this.idProperty];
            }
        }
        return null;
    };

    private getPrediction = (search: string): string | undefined => {
        const prediction = this.fuse.search(search).find(result => {
            if (result.score! > 0.5) {
                return false;
            }
            const targetString: string = result.item[this.labelProperty];
            const targetLower: string = targetString.toLowerCase();
            const searchLower: string = search.toLowerCase();
            return targetLower.startsWith(searchLower);
        });

        return prediction ? prediction.item[this.labelProperty] : undefined;
    };
}

export default SelectorInputFormatter;
