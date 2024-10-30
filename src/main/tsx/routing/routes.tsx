import React from 'react';

import {RouteObject} from 'react-router-dom';

import Skeleton from "@/components/Skeleton/Skeleton";
import DiagramView from "@/views/DiagramView/DiagramView";
import Stats from "@/views/StatsView/Stats";
import TrackingView from "@/views/TrackingView/TrackingView";

const routes: RouteObject[] = [
    {
        path: "/",
        element: <Skeleton/>,
        children: [
            {path: "", element: <></>},
            {path: "transactions", element: <TrackingView/>},
            {path: "stats", element: <Stats/>},
            {path: "diagrams", element: <DiagramView/>},
        ]
    }
];

export default routes;
