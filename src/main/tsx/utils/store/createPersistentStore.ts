import {create, StateCreator} from "zustand/index";
import {persist} from "zustand/middleware";

import createStorage, {CreateZustandStorageOptions, Serialized} from "@/utils/store/createStorage";

type PersistentStoreOptions<STATE, STATEDATA, SERIALIZED extends Serialized> = {
    name: string;
    stateCreator: StateCreator<STATE, [["zustand/persist", unknown]], []>
    storage: CreateZustandStorageOptions<STATEDATA, SERIALIZED>;
};

const createPersistentStore = <STATE, STATEDATA, SERIALIZED extends Serialized>(options: PersistentStoreOptions<STATE, STATEDATA, SERIALIZED>) => {
    const boundStore = create<STATE>()(
        persist(
            options.stateCreator,
            {
                name: options.name,
                storage: createStorage(options.storage),
            },
        ),
    );
    if (options.storage.storeInLocalStorage && options.storage.storeInUrl) {
        // synchronize, if both url and local storage are used
        boundStore.setState(boundStore.getState());
    }
    return boundStore();
};

export default createPersistentStore;
