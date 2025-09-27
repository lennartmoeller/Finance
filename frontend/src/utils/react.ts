import { memo as memoReact } from "react";
import type { ComponentType } from "react";

export function memo<T>(component: T, displayName: string): T {
    Object.assign(component as { displayName?: string }, { displayName });

    return memoReact(component as unknown as ComponentType) as unknown as T;
}
