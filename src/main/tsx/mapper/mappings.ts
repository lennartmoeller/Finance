export interface TypeMapper<A, B> {
    fromDTO: (value: B) => A;
    toDTO: (value: A) => B;
}

export const dateMapper: TypeMapper<Date, string> = {
    fromDTO: (value: string) => new Date(value),
    toDTO: (value: Date) => value.toISOString(),
};

export const dateMapperNullable: TypeMapper<Date | null, string | null> = {
    fromDTO: (value: string | null) => value === null ? null : new Date(value),
    toDTO: (value: Date | null) => value === null ? null : value.toISOString(),
};
