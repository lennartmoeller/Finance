import TypeMapper from "@/mapper/TypeMapper";

interface Account {
    id: number;
    label: string;
    startBalance: number;
    active: boolean;
}

export interface AccountDTO {
    id: number;
    label: string;
    startBalance: number;
    active: boolean;
}

export const accountMapper: TypeMapper<Account, AccountDTO> = {
    fromDTO: (dto: AccountDTO) => dto,
    toDTO: (model: Account) => model,
};

export default Account;
