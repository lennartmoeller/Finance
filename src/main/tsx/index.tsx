import React from 'react';

import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import ReactDOM from 'react-dom/client';

import Stats from "@/views/Stats/Stats";

import '@/index.css';
import Skeleton from "@/components/Skeleton/Skeleton";

const queryClient: QueryClient = new QueryClient();

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);

root.render(
    <React.StrictMode>
        <QueryClientProvider client={queryClient}>
            <Skeleton>
                <Stats/>
            </Skeleton>
        </QueryClientProvider>
    </React.StrictMode>
);
