import {create, StateCreator} from "zustand/index";
import {persist} from "zustand/middleware";

import createZustandStorage, {CreateZustandStorageOptions, Serialized} from "@/utils/store/createZustandStorage";

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
                storage: createZustandStorage(options.storage),
            },
        ),
    );
    return () => ({
        reinit: () => boundStore.setState(boundStore.getState()),
        ...boundStore()
    });
};

export default createPersistentStore;
