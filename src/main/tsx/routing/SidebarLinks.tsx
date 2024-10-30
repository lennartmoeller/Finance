import React from "react";

import {Link} from "react-router-dom";

const SidebarLinks: React.FC = () => (
    <div style={{display: "grid", gridAutoFlow: "row"}}>
        <Link to="/">Home</Link>
        <Link to="/transactions">Transactions</Link>
        <Link to="/stats">Statistics</Link>
        <Link to="/diagrams">Diagrams</Link>
    </div>
);

export default SidebarLinks;
