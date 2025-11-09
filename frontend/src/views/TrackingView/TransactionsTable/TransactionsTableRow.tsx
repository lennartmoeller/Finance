import React, { useMemo } from "react";

import Button from "@/components/Button/Button";
import useForm from "@/components/Form/hooks/useForm";
import Input from "@/components/Form/Input";
import CentInputFormatter from "@/components/Form/InputFormatter/CentInputFormatter";
import GermanDateInputFormatter from "@/components/Form/InputFormatter/GermanDateInputFormatter";
import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import StringInputFormatter from "@/components/Form/InputFormatter/StringInputFormatter";
import Icon from "@/components/Icon/Icon";
import TableCell from "@/components/Table/TableCell";
import { useDeleteTransaction, useSaveTransaction } from "@/services/transactions";
import Account from "@/types/Account";
import Category from "@/types/Category";
import Transaction from "@/types/Transaction";
import { Nullable } from "@/utils/types";
import useTransactionFilter from "@/views/TrackingView/stores/useTransactionFilter";

interface TransactionsTableRowProps {
    transaction: Nullable<Transaction>;
    accountInputFormatter: SelectorInputFormatter<Account, "id", "label">;
    categoryInputFormatter: SelectorInputFormatter<Category, "id", "label">;
    draft?: boolean;
}

const TransactionsTableRow: React.FC<TransactionsTableRowProps> = ({
    transaction,
    accountInputFormatter,
    categoryInputFormatter,
    draft = false,
}) => {
    const saveTransaction = useSaveTransaction();
    const deleteTransaction = useDeleteTransaction();

    const register = useForm({
        initial: transaction,
        onSuccess: saveTransaction,
        resetOnSuccess: draft,
    });

    const { yearMonths } = useTransactionFilter();

    const dateInputFormatter = useMemo(
        () =>
            new GermanDateInputFormatter({
                defaultYear: yearMonths.length === 1 ? yearMonths[0].getYear().getValue() : undefined,
                defaultMonth: yearMonths.length === 1 ? yearMonths[0].getMonth().getValue() : undefined,
                required: true,
            }),
        [yearMonths],
    );

    const descriptionInputFormatter = useMemo(() => new StringInputFormatter(), []);

    const amountInputFormatter = useMemo(() => new CentInputFormatter({ required: true }), []);

    return (
        <>
            <TableCell>
                <Input {...register("date")} inputFormatter={dateInputFormatter} autoFocus={draft} />
            </TableCell>
            <TableCell>
                <Input {...register("accountId")} inputFormatter={accountInputFormatter} />
            </TableCell>
            <TableCell>
                <Input {...register("categoryId")} inputFormatter={categoryInputFormatter} />
            </TableCell>
            <TableCell>
                <Input {...register("description")} inputFormatter={descriptionInputFormatter} />
            </TableCell>
            <TableCell horAlign="right">
                <Input {...register("amount")} inputFormatter={amountInputFormatter} textAlign="right" />
            </TableCell>
            <TableCell horAlign="center">
                {!draft && (
                    <Button onClick={() => deleteTransaction(transaction as Transaction)}>
                        <Icon id="fa-solid fa-trash" color="red" />
                    </Button>
                )}
            </TableCell>
        </>
    );
};

export default TransactionsTableRow;
