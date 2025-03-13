import React from 'react';

import DashboardView from "@/views/DashboardView/DashboardView";
import StatsView from "@/views/StatsView/StatsView";
import TrackingView from "@/views/TrackingView/TrackingView";

export interface Route {
    label: string;
    icon: string;
    element: React.ReactNode;
}

const routes: Record<string, Route> = {
    "/": {
        label: "Dashboard",
        icon: "fa-house",
        element: <DashboardView/>,
    },
    "/transactions": {
        label: "Transactions",
        icon: "fa-chart-simple",
        element: <TrackingView/>,
    },
    "/stats": {
        label: "Stats",
        icon: "fa-sack-dollar",
        element: <StatsView/>,
    },
};

export default routes;
