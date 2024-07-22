import {dateMapper, TypeMapper} from "@/mapper/mappings";

export interface Transaction {
    id: number;
    accountId: number;
    categoryId: number;
    date: Date;
    amount: number;
    description: string;
}

export interface TransactionDTO {
    id: number;
    accountId: number;
    categoryId: number;
    date: string;
    amount: number;
    description: string;
}

export const transactionMapper: TypeMapper<Transaction, TransactionDTO> = {
    fromDTO: (dto: TransactionDTO) => ({
        ...dto,
        date: dateMapper.fromDTO(dto.date),
    }),
    toDTO: (model: Transaction) => ({
        ...model,
        date: dateMapper.toDTO(model.date),
    }),
};