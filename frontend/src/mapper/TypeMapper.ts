interface TypeMapper<A, B> {
    fromDTO: (value: B) => A;
    toDTO: (value: A) => B;
}

export default TypeMapper;
