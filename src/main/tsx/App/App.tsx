import React, {useEffect, useState} from 'react';
import './App.css';
import {TimeLineChart} from '../TimeLineChart';

const getData = async (url: string) => {
    const response = await fetch(url);
    return response.json();
}

function App() {
    const [rawData, setRawData] = useState<Array<{ date: string, balance: number, smoothedBalance: number }>>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        (async () => {
            try {
                let result = await getData('/api/stats/days');
                result = result.map((item: {date: string, balance: number, smoothedBalance: number}) => {
                    return {
                        date: item.date,
                        balance: item.balance / 100,
                        smoothedBalance: item.smoothedBalance / 100
                    }
                })
                setRawData(result);
            } catch (error) {
                setError('Error fetching data');
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div>
            <TimeLineChart
                data={rawData}
                xProperty="date"
                yProperties={['balance', 'smoothedBalance']}
                alwaysShowXBar={true}
            />
        </div>
    );
}

export default App;
