import useItems, {UseItemsResult} from "@/services/util/useItems";
import AccountBalance, {AccountBalanceDTO, accountBalanceMapper} from "@/types/AccountBalance";

export const accountBalancesUrl = new URL("api/accountBalances", window.location.origin);

export const useAccountBalances = (): UseItemsResult<Array<AccountBalance>> => useItems({
    url: accountBalancesUrl,
    converter: (abs: Array<AccountBalanceDTO>) => abs.map(accountBalanceMapper.fromDTO),
});
