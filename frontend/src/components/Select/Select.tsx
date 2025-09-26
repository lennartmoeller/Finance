import React, { useState } from "react";

type Option<K> = {
    key: K;
    label: string;
};

type SelectProps<K, O extends Option<K>> = {
    options: Array<O>;
    onSelect: (selectedKey: K) => void;
    initialKey?: K;
};

const Select = <K, O extends Option<K>>({
    options,
    onSelect,
    initialKey,
}: SelectProps<K, O>) => {
    const [selectedKey, setSelectedKey] = useState<O["key"]>(
        initialKey || options[0].key,
    );

    const handleClick = (key: O["key"]) => {
        setSelectedKey(key);
        onSelect(key);
    };

    return (
        <div style={{ display: "flex", borderBottom: "2px solid #ccc" }}>
            {options.map((option: O) => (
                <div
                    key={JSON.stringify(option.key)}
                    onClick={() => handleClick(option.key)}
                    style={{
                        padding: "10px 20px",
                        cursor: "pointer",
                        borderBottom:
                            JSON.stringify(selectedKey) ===
                            JSON.stringify(option.key)
                                ? "2px solid black"
                                : "none",
                    }}
                >
                    {option.label}
                </div>
            ))}
        </div>
    );
};

export default Select;
