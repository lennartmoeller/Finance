import useItems, {UseItemsResult} from "@/services/util/useItems";
import AccountBalance, {AccountBalanceDTO, accountBalanceMapper} from "@/types/AccountBalance";
import {ExtURL} from "@/utils/ExtURL";

export const accountBalancesUrl = new ExtURL("api/accountBalances", window.location.origin);

export const useAccountBalances = (): UseItemsResult<Array<AccountBalance>> => useItems({
    url: accountBalancesUrl,
    converter: (abs: Array<AccountBalanceDTO>) => abs.map(accountBalanceMapper.fromDTO),
});
