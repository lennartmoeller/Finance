import React from 'react';

import {RouteObject} from 'react-router-dom';

import Skeleton from "@/components/Skeleton/Skeleton";
import Stats from "@/views/Stats/Stats";
import Transactions from "@/views/Transactions/Transactions";

const routes: RouteObject[] = [
    {
        path: "/",
        element: <Skeleton/>,
        children: [
            {path: "", element: <></>},
            {path: "transactions", element: <Transactions/>},
            {path: "stats", element: <Stats/>},
        ]
    }
];

export default routes;
