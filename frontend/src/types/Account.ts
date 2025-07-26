import TypeMapper from "@/mapper/TypeMapper";

interface Account {
    id: number;
    label: string;
    iban: string | null;
    startBalance: number;
    active: boolean;
    deposits: boolean;
}

export interface AccountDTO {
    id: number;
    label: string;
    iban: string | null;
    startBalance: number;
    active: boolean;
    deposits: boolean;
}

export const accountMapper: TypeMapper<Account, AccountDTO> = {
    fromDTO: (dto: AccountDTO) => dto,
    toDTO: (model: Account) => model,
};

export default Account;
