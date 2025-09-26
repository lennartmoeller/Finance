import { createAsyncStoragePersister } from "@tanstack/query-async-storage-persister";
import { type AsyncStorage } from "@tanstack/query-persist-client-core";
import { QueryClient } from "@tanstack/react-query";

export const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            gcTime: Infinity,
            staleTime: 1000 * 60 * 5, // 5 minutes
        },
    },
});
const createBrowserStorage = (): AsyncStorage | undefined => {
    if (typeof window === "undefined") {
        return undefined;
    }

    const { localStorage } = window;

    return {
        getItem: (key) => localStorage.getItem(key),
        setItem: (key, value) => localStorage.setItem(key, value),
        removeItem: (key) => localStorage.removeItem(key),
    };
};

const storage = createBrowserStorage();

export const persister = createAsyncStoragePersister({
    storage,
    key: "serverStore",
});
