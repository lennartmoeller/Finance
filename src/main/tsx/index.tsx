import React from 'react';

import '@/index.css';
import {PersistQueryClientProvider} from '@tanstack/react-query-persist-client';
import ReactDOM from 'react-dom/client';
import {BrowserRouter, useRoutes} from "react-router-dom";
import {ThemeProvider} from "styled-components";

import {persister, queryClient} from "@/config/queryClient";
import routes from "@/routing/routes";
import theme from "@/styles/theme";

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);

const AppRoutes = () => useRoutes(routes);

root.render(
    <React.StrictMode>
        <PersistQueryClientProvider
            client={queryClient}
            persistOptions={{persister}}
        >
            <ThemeProvider theme={theme}>
                <BrowserRouter>
                    <AppRoutes/>
                </BrowserRouter>
            </ThemeProvider>
        </PersistQueryClientProvider>
    </React.StrictMode>
);
