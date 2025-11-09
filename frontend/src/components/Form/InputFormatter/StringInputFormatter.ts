import InputFormatter from "@/components/Form/InputFormatter/InputFormatter";

class StringInputFormatter extends InputFormatter<string> {
    public valueToString(value: string | null): string {
        return value ?? "";
    }

    public stringToValue(string: string): string | null {
        return string;
    }
}

export default StringInputFormatter;
