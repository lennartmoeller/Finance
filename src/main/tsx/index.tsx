import React from 'react';

import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import ReactDOM from 'react-dom/client';

import Categories from "@/views/Categories/Categories";

import '@/index.css';

const queryClient: QueryClient = new QueryClient();

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);

root.render(
    <React.StrictMode>
        <QueryClientProvider client={queryClient}>
            <Categories/>
        </QueryClientProvider>
    </React.StrictMode>
);
