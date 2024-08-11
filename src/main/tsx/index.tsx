import React from 'react';

import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import ReactDOM from 'react-dom/client';
import '@/index.css';
import {BrowserRouter, useRoutes} from "react-router-dom";
import {ThemeProvider} from "styled-components";

import routes from "@/routing/routes";
import theme from "@/styles/theme";

const queryClient: QueryClient = new QueryClient();

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);

const AppRoutes = () => {
    return useRoutes(routes);
};

root.render(
    <React.StrictMode>
        <QueryClientProvider client={queryClient}>
            <ThemeProvider theme={theme}>
                <BrowserRouter>
                    <AppRoutes/>
                </BrowserRouter>
            </ThemeProvider>
        </QueryClientProvider>
    </React.StrictMode>
);
