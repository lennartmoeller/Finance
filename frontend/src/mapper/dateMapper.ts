import TypeMapper from "@/mapper/TypeMapper";
import { dateToString } from "@/utils/date";

const dateMapper: TypeMapper<Date, string> = {
    fromDTO: (value: string) => new Date(value),
    toDTO: (value: Date) => dateToString(value),
};

export default dateMapper;
