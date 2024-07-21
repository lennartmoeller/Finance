import {Account} from "@/types/Account";
import {Category} from "@/types/Category";
import {CategoryDTO} from "@/types/CategoryDTO";
import {Transaction} from "@/types/Transaction";
import {TransactionDTO} from "@/types/TransactionDTO";

interface TypeMapper<A, B> {
    fromDTO: (value: B) => A;
    toDTO: (value: A) => B;
}

const dateTypeMapper: TypeMapper<Date, string> = {
    fromDTO: (value: string) => new Date(value),
    toDTO: (value: Date) => value.toISOString(),
};

export const accountMapper: TypeMapper<Account, Account> = {
    fromDTO: (dto: Account) => dto,
    toDTO: (model: Account) => model,
}

export const categoryMapper: TypeMapper<Category, CategoryDTO> = {
    fromDTO: (dto: CategoryDTO) => ({
        ...dto,
        start: dateTypeMapper.fromDTO(dto.start),
        end: dateTypeMapper.fromDTO(dto.end),
    }),
    toDTO: (model: Category) => ({
        ...model,
        start: dateTypeMapper.toDTO(model.start),
        end: dateTypeMapper.toDTO(model.end),
    }),
}

export const transactionMapper: TypeMapper<Transaction, TransactionDTO> = {
    fromDTO: (dto: TransactionDTO) => ({
        ...dto,
        date: dateTypeMapper.fromDTO(dto.date),
    }),
    toDTO: (model: Transaction) => ({
        ...model,
        date: dateTypeMapper.toDTO(model.date),
    }),
}
