import useItems, {UseItemsResult} from "@/services/util/useItems";
import Account, {AccountDTO, accountMapper} from "@/types/Account";

export const accountsUrl = new URL("api/accounts", window.location.origin);

export const useAccounts = (): UseItemsResult<Array<Account>> => useItems({
    url: accountsUrl,
    converter: (as: Array<AccountDTO>) => as.map(accountMapper.fromDTO),
});
