import * as React from 'react';

import { getTheme } from '@table-library/react-table-library/baseline';
import { Body, Cell, Header, HeaderCell, HeaderRow, Row, Table } from '@table-library/react-table-library/table';
import { useTheme } from '@table-library/react-table-library/theme';

interface DataItem {
    id: string;  // Add id property
    date: string;
    balance: number;
    smoothedBalance: number;
}

interface TimeLineChartProps {
    data: DataItem[];
}

export const ExampleTable: React.FC<TimeLineChartProps> = ({ data }) => {
    const theme = useTheme(getTheme());

    return (
        <Table data={{ nodes: data }} theme={theme}>
            {(tableList: DataItem[]) => (
                <>
                    <Header>
                        <HeaderRow>
                            <HeaderCell>Date</HeaderCell>
                            <HeaderCell>Balance</HeaderCell>
                            <HeaderCell>Smoothed Balance</HeaderCell>
                        </HeaderRow>
                    </Header>

                    <Body>
                        {tableList.map((item: DataItem) => (
                            <Row key={item.id} item={item}> {/* Use item.id as the key */}
                                <Cell>{item.date}</Cell>
                                <Cell>{item.balance}</Cell>
                                <Cell>{item.smoothedBalance}</Cell>
                            </Row>
                        ))}
                    </Body>
                </>
            )}
        </Table>
    );
};
