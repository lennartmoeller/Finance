import TypeMapper from "@/mapper/TypeMapper";

const dateMapper: TypeMapper<Date, string> = {
    fromDTO: (value: string) => new Date(value),
    toDTO: (value: Date) => value.toISOString(),
};

export default dateMapper;
