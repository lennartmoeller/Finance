import TypeMapper from "@/mapper/TypeMapper";
import { dateToString } from "@/utils/date";

const dateMapperNullable: TypeMapper<Date | null, string | null> = {
    fromDTO: (value: string | null) => (value === null ? null : new Date(value)),
    toDTO: (value: Date | null) => (value === null ? null : dateToString(value)),
};

export default dateMapperNullable;
