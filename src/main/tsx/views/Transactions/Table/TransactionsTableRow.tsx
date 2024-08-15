import React from 'react';

import useForm from "@/components/Form/hooks/useForm";
import Input from "@/components/Form/Input";
import CentInputFormatter from "@/components/Form/InputFormatter/CentInputFormatter";
import DateInputFormatter from "@/components/Form/InputFormatter/DateInputFormatter";
import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import StringInputFormatter from "@/components/Form/InputFormatter/StringInputFormatter";
import TableBodyCell from "@/components/Table/TableBodyCell";
import TableRow from "@/components/Table/TableRow";
import Account from "@/types/Account";
import Category from "@/types/Category";
import Transaction from "@/types/Transaction";
import {Nullable} from "@/utils/types";

interface TransactionsTableRowProps {
    transaction: Transaction;
    accountsSelectorInputFormatter: SelectorInputFormatter<Account, "id", "label">;
    categoriesSelectorInputFormatter: SelectorInputFormatter<Category, "id", "label">;
}

const TransactionsTableRow: React.FC<TransactionsTableRowProps> = ({
                                                                       transaction,
                                                                       accountsSelectorInputFormatter,
                                                                       categoriesSelectorInputFormatter,
                                                                   }) => {
    const register = useForm({
        initial: transaction,
        onChange: (item: Nullable<Transaction>) => {
            console.log("Full item", item);
            return item;
        },
    });

    return (
        <TableRow>
            <TableBodyCell>
                <Input
                    {...register("date")}
                    inputFormatter={new DateInputFormatter()}
                />
            </TableBodyCell>
            <TableBodyCell>
                <Input
                    {...register("accountId")}
                    inputFormatter={accountsSelectorInputFormatter}
                />
            </TableBodyCell>
            <TableBodyCell>
                <Input
                    {...register("categoryId")}
                    inputFormatter={categoriesSelectorInputFormatter}
                />
            </TableBodyCell>
            <TableBodyCell width={300}>
                <Input
                    {...register("description")}
                    inputFormatter={new StringInputFormatter()}
                />
            </TableBodyCell>
            <TableBodyCell horAlign="right" width={100}>
                <Input
                    {...register("amount")}
                    inputFormatter={new CentInputFormatter()}
                    textAlign="right"
                />
            </TableBodyCell>
        </TableRow>
    );
};

export default TransactionsTableRow;
