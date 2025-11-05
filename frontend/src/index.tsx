import React from "react";

import { PersistQueryClientProvider } from "@tanstack/react-query-persist-client";
import ReactDOM from "react-dom/client";
import { BrowserRouter, useRoutes } from "react-router-dom";
import type { RouteObject } from "react-router-dom";
import { ThemeProvider } from "styled-components";

import { persister, queryClient } from "@/config/queryClient";
import routes from "@/skeleton/routes";
import Skeleton from "@/skeleton/Skeleton";
import GlobalStyles from "@/styles/GlobalStyles";
import theme from "@/styles/theme";

const root = ReactDOM.createRoot(document.getElementById("root") as HTMLElement);

const AppRoutes = () =>
    useRoutes([
        {
            path: "/",
            element: <Skeleton />,
            children: Object.entries(routes).map(([path, { element }]): RouteObject => ({ path, element })),
        },
    ]);

root.render(
    <React.StrictMode>
        <PersistQueryClientProvider client={queryClient} persistOptions={{ persister }}>
            <ThemeProvider theme={theme}>
                <GlobalStyles />
                <BrowserRouter>
                    <AppRoutes />
                </BrowserRouter>
            </ThemeProvider>
        </PersistQueryClientProvider>
    </React.StrictMode>,
);
