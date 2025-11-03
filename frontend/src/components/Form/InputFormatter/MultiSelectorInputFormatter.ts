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

        const labels = value
            .map((id) => {
                const option = this.options.find(
                    (opt) => opt[this.idProperty] === id,
                );
                return option ? option[this.labelProperty] : null;
            })
            .filter((label) => label !== null) as string[];

        return labels.join(MultiSelectorInputFormatter.SEGMENT_SEPARATOR);
    }

    public stringToValue(string: string): T[K][] | null {
        if (!string.trim()) {
            return null;
        }

        const segments = this.parseSegments(string);
        const ids: T[K][] = [];

        for (const segment of segments) {
            const prediction = this.getPrediction(segment);
            if (prediction) {
                ids.push(prediction[this.idProperty]);
            }
        }

        return ids.length > 0 ? ids : null;
    }

    public onChange(
        before: InputState<T[K][]>,
        after: string,
    ): InputState<T[K][]> {
        const justTypedComma =
            after.endsWith(",") &&
            !before.value.endsWith(",") &&
            before.prediction;

        if (justTypedComma && before.prediction) {
            const beforeSegments = this.parseSegments(before.value);
            const confirmedSegments =
                beforeSegments.length > 1 ? beforeSegments.slice(0, -1) : [];

            const predictionLabel = before.prediction.label;
            const lastCommaIndex = predictionLabel.lastIndexOf(
                MultiSelectorInputFormatter.SEGMENT_SEPARATOR,
            );
            const predictedSegmentLabel =
                lastCommaIndex !== -1
                    ? predictionLabel.substring(
                          lastCommaIndex +
                              MultiSelectorInputFormatter.SEGMENT_SEPARATOR_LENGTH,
                      )
                    : predictionLabel;

            if (confirmedSegments.length > 0) {
                after =
                    confirmedSegments.join(
                        MultiSelectorInputFormatter.SEGMENT_SEPARATOR,
                    ) +
                    MultiSelectorInputFormatter.SEGMENT_SEPARATOR +
                    predictedSegmentLabel +
                    MultiSelectorInputFormatter.SEGMENT_SEPARATOR;
            } else {
                after =
                    predictedSegmentLabel +
                    MultiSelectorInputFormatter.SEGMENT_SEPARATOR;
            }
        } else if (
            after.endsWith(",") &&
            !after.endsWith(MultiSelectorInputFormatter.SEGMENT_SEPARATOR) &&
            after.length > before.value.length
        ) {
            after = after + " ";
        }

        const segments = this.parseSegments(after);
        const activeSegment = this.getActiveSegment(after);

        const prediction = this.getPrediction(activeSegment);

        const result: InputState<T[K][]> = super.onChange(before, after);

        if (prediction) {
            const confirmedSegments = segments.slice(0, -1);
            const predictedLabel = prediction[this.labelProperty];
            const fullPrediction =
                confirmedSegments.length > 0
                    ? confirmedSegments.join(
                          MultiSelectorInputFormatter.SEGMENT_SEPARATOR,
                      ) +
                      MultiSelectorInputFormatter.SEGMENT_SEPARATOR +
                      predictedLabel
                    : predictedLabel;

            const confirmedIds: T[K][] = [];
            for (const segment of confirmedSegments) {
                const pred = this.getPrediction(segment);
                if (pred) {
                    confirmedIds.push(pred[this.idProperty]);
                }
            }
            const predictedIds = [...confirmedIds, prediction[this.idProperty]];

            const valueWithConfirmedSegments =
                confirmedSegments.length > 0
                    ? confirmedSegments.join(
                          MultiSelectorInputFormatter.SEGMENT_SEPARATOR,
                      ) +
                      MultiSelectorInputFormatter.SEGMENT_SEPARATOR +
                      (predictedLabel as string).slice(0, activeSegment.length)
                    : (predictedLabel as string).slice(0, activeSegment.length);

            return {
                ...result,
                value: valueWithConfirmedSegments,
                prediction: {
                    label: fullPrediction,
                    value: predictedIds,
                },
            };
        }

        return result;
    }

    private parseSegments(input: string): string[] {
        return input
            .split(MultiSelectorInputFormatter.SEGMENT_SEPARATOR)
            .map((segment) => segment.trim())
            .filter((segment) => segment.length > 0);
    }

    private getActiveSegment(input: string): string {
        const lastCommaIndex = input.lastIndexOf(
            MultiSelectorInputFormatter.SEGMENT_SEPARATOR,
        );
        if (lastCommaIndex === -1) {
            return input.trim();
        }
        return input
            .substring(
                lastCommaIndex +
                    MultiSelectorInputFormatter.SEGMENT_SEPARATOR_LENGTH,
            )
            .trim();
    }
}

export default MultiSelectorInputFormatter;
