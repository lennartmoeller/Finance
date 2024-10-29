import useItems, {UseItemsResult} from "@/services/util/useItems";
import Account, {AccountDTO, accountMapper} from "@/types/Account";
import {ExtURL} from "@/utils/ExtURL";

export const accountsUrl = new ExtURL("api/accounts", window.location.origin);

export const useAccounts = (): UseItemsResult<Array<Account>> => useItems({
    url: accountsUrl,
    converter: (as: Array<AccountDTO>) => as.map(accountMapper.fromDTO),
});
