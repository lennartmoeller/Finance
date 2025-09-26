export type Nullable<T> = {
    [P in keyof T]: T[P] | null;
};

export type Optional<T> = {
    [P in keyof T]?: T[P];
};

export type NullableAndOptional<T> = {
    [P in keyof T]?: T[P] | null;
};
