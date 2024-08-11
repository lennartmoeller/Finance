import React from "react";

import {Link} from "react-router-dom";

const SidebarLinks: React.FC = () => (
    <>
        <Link to="/">Home</Link>
        <Link to="/transactions">Transactions</Link>
        <Link to="/stats">Statistics</Link>
    </>
);

export default SidebarLinks;
