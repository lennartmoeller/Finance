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

interface TransactionsTableProps {
    accounts: Array<Account>;
    categories: Array<Category>;
    transactions: Array<Transaction>;
}

const TransactionsTable: React.FC<TransactionsTableProps> = ({accounts, categories, transactions,}) => {
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
                        onChange={(item: Transaction) => {
                            console.log("Full item", item);
                            return item;
                        }}
                    >
                        <TableBodyCell>{transaction.date.toDateString()}</TableBodyCell>
                        <TableBodyCell>
                            <Input
                                property="accountId"
                                inputFormatter={new SelectorInputFormatter(accounts, "id", "label")}
                            />
                        </TableBodyCell>
                        <TableBodyCell>
                            <Input
                                property="categoryId"
                                inputFormatter={new SelectorInputFormatter(categories, "id", "label")}
                            />
                        </TableBodyCell>
                        <TableBodyCell width={300}>
                            <Input
                                property="description"
                                inputFormatter={new StringInputFormatter()}
                            />
                        </TableBodyCell>
                        <TableBodyCell horAlign="right" width={100}>
                            <Input
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
