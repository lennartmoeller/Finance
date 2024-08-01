import TypeMapper from "@/mapper/TypeMapper";

const dateMapperNullable: TypeMapper<Date | null, string | null> = {
    fromDTO: (value: string | null) => value === null ? null : new Date(value),
    toDTO: (value: Date | null) => value === null ? null : value.toISOString(),
};

export default dateMapperNullable;
