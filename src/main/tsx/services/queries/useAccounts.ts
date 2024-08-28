import useGetQuery, {UseGetQueryResult} from "@/services/queries/util/useGetQuery";
import Account, {AccountDTO, accountMapper} from "@/types/Account";

const useAccounts = (): UseGetQueryResult<Array<Account>> => useGetQuery({
    url: "/api/accounts",
    converter: (as: Array<AccountDTO>) => as.map(accountMapper.fromDTO),
});

export default useAccounts;
