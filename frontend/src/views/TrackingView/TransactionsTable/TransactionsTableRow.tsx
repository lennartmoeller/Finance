import React, { useMemo } from "react";

import Button from "@/components/Button/Button";
import useForm from "@/components/Form/hooks/useForm";
import Input from "@/components/Form/Input";
import CentInputFormatter from "@/components/Form/InputFormatter/CentInputFormatter";
import GermanDateInputFormatter from "@/components/Form/InputFormatter/GermanDateInputFormatter";
import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import StringInputFormatter from "@/components/Form/InputFormatter/StringInputFormatter";
import Icon from "@/components/Icon/Icon";
import TableBodyCell from "@/components/Table/TableBodyCell";
import TableRow from "@/components/Table/TableRow";
import {
    useDeleteTransaction,
    useSaveTransaction,
} from "@/services/transactions";
import Account from "@/types/Account";
import Category from "@/types/Category";
import Transaction from "@/types/Transaction";
import { Nullable } from "@/utils/types";
import useFocusedTransaction from "@/views/TrackingView/stores/useFocusedTransaction";
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
    const setFocusedTransaction = useFocusedTransaction(
        (state) => state.setFocusedTransaction,
    );

    const register = useForm({
        initial: transaction,
        onSuccess: saveTransaction,
        resetOnSuccess: draft,
    });

    const { yearMonths } = useTransactionFilter();

    const dateInputFormatter = useMemo(
        () =>
            new GermanDateInputFormatter({
                defaultYear:
                    yearMonths.length !== 1
                        ? undefined
                        : yearMonths[0].getYear().getValue(),
                defaultMonth:
                    yearMonths.length !== 1
                        ? undefined
                        : yearMonths[0].getMonth().getValue(),
                required: true,
            }),
        [yearMonths],
    );

    const descriptionInputFormatter = useMemo(
        () => new StringInputFormatter(),
        [],
    );

    const amountInputFormatter = useMemo(
        () => new CentInputFormatter({ required: true }),
        [],
    );

    return (
        <TableRow
            onFocus={() => setFocusedTransaction(transaction as Transaction)}
            onBlur={() => setFocusedTransaction(null)}
        >
            <TableBodyCell width={98}>
                <Input
                    {...register("date")}
                    inputFormatter={dateInputFormatter}
                    autoFocus={draft}
                />
            </TableBodyCell>
            <TableBodyCell width={140}>
                <Input
                    {...register("accountId")}
                    inputFormatter={accountInputFormatter}
                />
            </TableBodyCell>
            <TableBodyCell width={200}>
                <Input
                    {...register("categoryId")}
                    inputFormatter={categoryInputFormatter}
                />
            </TableBodyCell>
            <TableBodyCell width={350}>
                <Input
                    {...register("description")}
                    inputFormatter={descriptionInputFormatter}
                />
            </TableBodyCell>
            <TableBodyCell horAlign="right" width={100}>
                <Input
                    {...register("amount")}
                    inputFormatter={amountInputFormatter}
                    textAlign="right"
                />
            </TableBodyCell>
            <TableBodyCell horAlign="center" width={31}>
                {!draft && (
                    <Button
                        onClick={() =>
                            deleteTransaction(transaction as Transaction)
                        }
                    >
                        <Icon id="fa-solid fa-trash" color="red" />
                    </Button>
                )}
            </TableBodyCell>
        </TableRow>
    );
};

export default TransactionsTableRow;
