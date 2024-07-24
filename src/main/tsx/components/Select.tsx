import React, {useState} from 'react';

type Option = {
    key: string;
    label: string;
};

type SelectProps<T extends Option> = {
    options: T[];
    onSelect: (selectedKey: T['key']) => void;
    initialKey?: T['key'];
};

const Select = <T extends Option>({options, onSelect, initialKey}: SelectProps<T>) => {
    const [selectedKey, setSelectedKey] = useState<T['key']>(initialKey || options[0].key);

    const handleClick = (key: T['key']) => {
        setSelectedKey(key);
        onSelect(key);
    };

    return (
        <div style={{display: 'flex', borderBottom: '2px solid #ccc'}}>
            {options.map((option: T) => (
                <div
                    key={option.key}
                    onClick={() => handleClick(option.key)}
                    style={{
                        padding: '10px 20px',
                        cursor: 'pointer',
                        borderBottom: selectedKey === option.key ? '2px solid black' : 'none'
                    }}
                >
                    {option.label}
                </div>
            ))}
        </div>
    );
};

export default Select;
