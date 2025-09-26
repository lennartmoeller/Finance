import React, { useState } from "react";

import Button from "@/components/Button/Button";

interface SwitchProps {
    content: (checked: boolean) => React.ReactNode;
    initial: boolean;
    onChange: (checked: boolean) => void;
}

const Switch: React.FC<SwitchProps> = ({ content, initial, onChange }) => {
    const [checked, setChecked] = useState(initial);

    const handleChange = () => {
        setChecked(!checked);
        onChange(!checked);
    };

    return <Button onClick={handleChange}>{content(checked)}</Button>;
};

export default Switch;
