import React from "react";

import Form from "@/components/Form/Form";
import Input from "@/components/Form/Input";
import CentInputFormatter from "@/components/Form/InputFormatter/CentInputFormatter";
import SelectorInputFormatter from "@/components/Form/InputFormatter/SelectorInputFormatter";
import StringInputFormatter from "@/components/Form/InputFormatter/StringInputFormatter";
import Table from "@/components/Table/Table";
import TableBodyCell from "@/components/Table/TableBodyCell";
import TableHeaderCell from "@/components/Table/TableHeaderCell";
import TableRow from "@/components/Table/TableRow";
import Account from "@/types/Account";
import Category from "@/types/Category";
import Transaction from "@/types/Transaction";
import {Nullable} from "@/utils/types";
import DateInputFormatter from "@/components/Form/InputFormatter/DateInputFormatter";

interface TransactionsTableProps {
    accounts: Array<Account>;
    categories: Array<Category>;
    transactions: Array<Transaction>;
}

const TransactionsTable: React.FC<TransactionsTableProps> = ({accounts, categories, transactions,}) => {
    const accountsSelectorInputFormatter = new SelectorInputFormatter(accounts, "id", "label");
    const categoriesSelectorInputFormatter = new SelectorInputFormatter(categories, "id", "label");

    return (
        <Table
            data={transactions}
            header={(
                <TableRow>
                    <TableHeaderCell>Date</TableHeaderCell>
                    <TableHeaderCell>Account</TableHeaderCell>
                    <TableHeaderCell>Category</TableHeaderCell>
                    <TableHeaderCell>Description</TableHeaderCell>
                    <TableHeaderCell horAlign="center">Amount</TableHeaderCell>
                </TableRow>
            )}
            body={(transaction: Transaction) => (
                <TableRow>
                    <Form
                        initial={transaction}
                        onChange={(item: Nullable<Transaction>) => {
                            console.log("Full item", item);
                            return item;
                        }}
                    >
                        <TableBodyCell>
                            <Input<Transaction, "date">
                                property="date"
                                inputFormatter={new DateInputFormatter()}
                            />
                        </TableBodyCell>
                        <TableBodyCell>
                            <Input<Transaction, "accountId">
                                property="accountId"
                                inputFormatter={accountsSelectorInputFormatter}
                            />
                        </TableBodyCell>
                        <TableBodyCell>
                            <Input<Transaction, "categoryId">
                                property="categoryId"
                                inputFormatter={categoriesSelectorInputFormatter}
                            />
                        </TableBodyCell>
                        <TableBodyCell width={300}>
                            <Input<Transaction, "description">
                                property="description"
                                inputFormatter={new StringInputFormatter()}
                            />
                        </TableBodyCell>
                        <TableBodyCell horAlign="right" width={100}>
                            <Input<Transaction, "amount">
                                property="amount"
                                inputFormatter={new CentInputFormatter()}
                                textAlign="right"
                            />
                        </TableBodyCell>
                    </Form>
                </TableRow>
            )}
        />
    );
};

export default TransactionsTable;
