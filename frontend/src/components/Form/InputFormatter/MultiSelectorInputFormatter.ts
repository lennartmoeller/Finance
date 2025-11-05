import BaseSelectorInputFormatter from "@/components/Form/InputFormatter/BaseSelectorInputFormatter";
import InputState from "@/components/Form/types/InputState";

class MultiSelectorInputFormatter<
    T extends Record<K, number> & Record<L, string>,
    K extends keyof T & string,
    L extends keyof T & string,
> extends BaseSelectorInputFormatter<T, K, L, T[K][]> {
    private static readonly SEGMENT_SEPARATOR = ", ";
    private static readonly SEGMENT_SEPARATOR_LENGTH = 2;

    public valueToString(value: T[K][] | null): string {
        if (!value || value.length === 0) {
            return "";
        }

        return value
            .map(id => this.options.find((opt) => opt[this.idProperty] === id))
            .filter(Boolean)
            .map((option) => option![this.labelProperty])
            .join(MultiSelectorInputFormatter.SEGMENT_SEPARATOR);
    }

    public stringToValue(string: string): T[K][] | null {
        if (!string.trim()) {
            return null;
        }

        const ids: T[K][] = this.parseSegments(string)
            .map((segment) => this.getPrediction(segment))
            .filter((prediction) => prediction !== null)
            .map((prediction) => prediction[this.idProperty]);

        return ids.length > 0 ? ids : null;
    }

    public onChange(before: InputState<T[K][]>, after: string): InputState<T[K][]> {
        if (after.endsWith(",") && !before.value.endsWith(",") && before.prediction) {
            const lastSegmentLabel = before.prediction.label.substring(
                before.prediction.label.lastIndexOf(MultiSelectorInputFormatter.SEGMENT_SEPARATOR) +
                    MultiSelectorInputFormatter.SEGMENT_SEPARATOR_LENGTH,
            );
            const confirmedSegments = this.parseSegments(before.value).slice(0, -1);
            after = this.joinSegments([...confirmedSegments, lastSegmentLabel, ""]);
        } else if (after.endsWith(",") && !after.endsWith(MultiSelectorInputFormatter.SEGMENT_SEPARATOR)) {
            after += " ";
        }

        const activeSegment = this.getActiveSegment(after);
        const prediction = this.getPrediction(activeSegment);

        const result = super.onChange(before, after);

        if (!prediction) {
            return result;
        }

        const confirmedSegments = this.parseSegments(after).slice(0, -1);
        const confirmedIds = confirmedSegments
            .map((segment) => this.getPrediction(segment))
            .filter(Boolean)
            .map((pred) => pred![this.idProperty]);

        const fullPredictionLabel = this.joinSegments([...confirmedSegments, prediction[this.labelProperty]]);
        const partialPredictionLabel = (prediction[this.labelProperty] as string).slice(0, activeSegment.length);
        const valueWithPrediction = this.joinSegments([...confirmedSegments, partialPredictionLabel]);

        return {
            ...result,
            value: valueWithPrediction,
            prediction: {
                label: fullPredictionLabel,
                value: [...confirmedIds, prediction[this.idProperty]],
            },
        };
    }

    private parseSegments(input: string): string[] {
        return input
            .split(MultiSelectorInputFormatter.SEGMENT_SEPARATOR)
            .map((segment) => segment.trim())
            .filter((segment) => segment.length > 0);
    }

    private getActiveSegment(input: string): string {
        const lastCommaIndex = input.lastIndexOf(MultiSelectorInputFormatter.SEGMENT_SEPARATOR);
        if (lastCommaIndex === -1) {
            return input.trim();
        }
        return input.substring(lastCommaIndex + MultiSelectorInputFormatter.SEGMENT_SEPARATOR_LENGTH).trim();
    }
}

export default MultiSelectorInputFormatter;
