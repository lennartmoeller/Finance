import React from 'react';

import useForm from "@/components/Form/hooks/useForm";
import Input from "@/components/Form/Input";
import CentInputFormatter from "@/components/Form/InputFormatter/CentInputFormatter";
import GermanDateInputFormatter from "@/components/Form/InputFormatter/GermanDateInputFormatter";
import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import StringInputFormatter from "@/components/Form/InputFormatter/StringInputFormatter";
import TableBodyCell from "@/components/Table/TableBodyCell";
import TableRow from "@/components/Table/TableRow";
import Account from "@/types/Account";
import Category from "@/types/Category";
import Transaction from "@/types/Transaction";

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
        onChange: (formState) => {
            console.log(formState);
        },
    });

    return (
        <TableRow>
            <TableBodyCell width={98}>
                <Input
                    {...register("date")}
                    inputFormatter={new GermanDateInputFormatter(2024, 12)}
                    required
                />
            </TableBodyCell>
            <TableBodyCell width={140}>
                <Input
                    {...register("accountId")}
                    inputFormatter={accountsSelectorInputFormatter}
                    required
                />
            </TableBodyCell>
            <TableBodyCell width={200}>
                <Input
                    {...register("categoryId")}
                    inputFormatter={categoriesSelectorInputFormatter}
                    required
                />
            </TableBodyCell>
            <TableBodyCell width={350}>
                <Input
                    {...register("description")}
                    inputFormatter={new StringInputFormatter()}
                />
            </TableBodyCell>
            <TableBodyCell horAlign="right" width={100}>
                <Input
                    {...register("amount")}
                    inputFormatter={new CentInputFormatter()}
                    required
                    textAlign="right"
                />
            </TableBodyCell>
        </TableRow>
    );
};

export default TransactionsTableRow;
