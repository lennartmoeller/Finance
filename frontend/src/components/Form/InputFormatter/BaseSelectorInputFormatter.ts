import Fuse from "fuse.js";

import InputFormatter, {
    InputFormatterOptions,
} from "@/components/Form/InputFormatter/InputFormatter";

export interface SelectorInputFormatterOptions<T, K, L>
    extends InputFormatterOptions {
    options: Array<T>;
    idProperty: K;
    labelProperty: L;
}

abstract class BaseSelectorInputFormatter<
    T extends Record<K, number> & Record<L, string>,
    K extends keyof T & string,
    L extends keyof T & string,
    V,
> extends InputFormatter<V> {
    protected static readonly FUSE_SCORE_THRESHOLD = 0.5;

    protected readonly options: Array<T>;
    protected readonly idProperty: K;
    protected readonly labelProperty: L;
    protected readonly fuse: Fuse<T>;

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

    protected getPrediction(search: string): T | null {
        const prediction = this.fuse.search(search).find((result) => {
            if (
                result.score! > BaseSelectorInputFormatter.FUSE_SCORE_THRESHOLD
            ) {
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
    }
}

export default BaseSelectorInputFormatter;
