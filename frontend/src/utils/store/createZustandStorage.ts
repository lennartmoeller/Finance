import { PersistStorage, StorageValue } from "zustand/middleware";

import { ExtURL } from "@/utils/ExtURL";

export type Serialized = string | Record<string, string>;

export interface CreateZustandStorageOptions<
    STATE,
    SERIALIZED extends Serialized,
> {
    storeInUrl?: boolean;
    storeInLocalStorage?: boolean;
    serialize?: (state: STATE) => SERIALIZED;
    parse?: (stringValue: SERIALIZED) => STATE;
}

const createZustandStorage = <STATE, SERIALIZED extends Serialized>({
    storeInLocalStorage = false,
    storeInUrl = false,
    serialize = (state: STATE): SERIALIZED => state as unknown as SERIALIZED,
    parse = (serialized: SERIALIZED): STATE => serialized as unknown as STATE,
}: CreateZustandStorageOptions<STATE, SERIALIZED>): PersistStorage<STATE> => ({
    getItem: (name: string): StorageValue<STATE> | null => {
        const getSerialized = (): SERIALIZED | null => {
            if (storeInUrl) {
                const url = new ExtURL();
                if (url.hasSearchParam(name)) {
                    return url.getSearchParam(name) as SERIALIZED;
                }
                if (url.hasSearchParamMap(name)) {
                    return url.getSearchParamMap(name) as SERIALIZED;
                }
            }
            if (storeInLocalStorage) {
                const stringValue: string | null = localStorage.getItem(name);
                if (stringValue) {
                    return JSON.parse(stringValue) as SERIALIZED;
                }
            }
            return null;
        };
        const serialized: SERIALIZED | null = getSerialized();
        if (serialized === null) {
            return null;
        }
        const state: STATE = parse(serialized);
        return { state };
    },
    setItem: (name: string, value: StorageValue<STATE>): void => {
        const serialized: SERIALIZED = serialize(value.state);
        if (storeInUrl) {
            const url = new ExtURL();
            if (serialized === null) {
                url.deleteSearchParam(name);
            } else if (typeof serialized === "string") {
                url.setSearchParam(name, serialized);
            } else {
                url.setSearchParamMap(name, serialized);
            }
            url.toCurrent();
        }
        if (storeInLocalStorage) {
            if (serialized === null) {
                localStorage.removeItem(name);
            } else if (typeof serialized === "string") {
                localStorage.setItem(name, serialized);
            } else {
                const stringValue: string = JSON.stringify(serialized);
                localStorage.setItem(name, stringValue);
            }
        }
    },
    removeItem: (name: string): void => {
        if (storeInLocalStorage) {
            localStorage.removeItem(name);
        }
        if (storeInUrl) {
            const url = new ExtURL();
            url.deleteSearchParam(name);
            url.deleteSearchParamMap(name);
            url.toCurrent();
        }
    },
});

export default createZustandStorage;
