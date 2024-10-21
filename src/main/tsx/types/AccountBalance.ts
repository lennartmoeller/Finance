import TypeMapper from "@/mapper/TypeMapper";

interface AccountBalance {
    accountId: number;
    balance: number;
}

export interface AccountBalanceDTO {
    accountId: number;
    balance: number;
}

export const accountBalanceMapper: TypeMapper<AccountBalance, AccountBalanceDTO> = {
    fromDTO: (dto: AccountBalanceDTO) => dto,
    toDTO: (model: AccountBalance) => model,
};

export default AccountBalance;
