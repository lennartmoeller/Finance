import {createContext, Dispatch, SetStateAction} from 'react';

const HierarchyContext = createContext<{
    level: number,
    childrenVisible: [boolean, Dispatch<SetStateAction<boolean>>],
    hasChildren: [boolean, Dispatch<SetStateAction<boolean>>],
}>({
    level: 0,
    childrenVisible: [true, () => void 0],
    hasChildren: [false, () => void 0],
});

export default HierarchyContext;
