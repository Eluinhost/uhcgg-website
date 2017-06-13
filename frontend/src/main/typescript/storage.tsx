import * as localForage from 'localforage';
import {Store} from "redux";
import {AppStore} from "./AppStore";
import {SidebarActions} from "./actions";
import ApolloClient from "apollo-client/ApolloClient";

interface WatchSelector<S, T>{
    (state: S): T
}

interface WatchCallback<S, T> {
    (newValue: T, oldValue: T, state: S): void
}

function watch<S, T>(store: Store<S>, selector: WatchSelector<S, T>, callback: WatchCallback<S, T>) {
    let current: T = selector(store.getState());

    store.subscribe(() => {
        const state = store.getState();
        const next = selector(state);

        if (next !== current) {
            callback(next, current, state);
            current = next
        }
    });
}

function initialState(sidebarCollapsed: boolean, apolloState: any): AppStore.All {
    return {
        sidebar: {
            collapsed: sidebarCollapsed
        },
        apollo: apolloState
    }
}

export function generateInitialState(store: Store<AppStore.All>, apollo: ApolloClient): Promise<AppStore.All> {
    localForage.config({ // TODO configure via webpack build
        name        : 'uhcgg_website',
        version     : 1.0,
        storeName   : 'uhcgg_website',
    });

    return localForage
        .getItem<boolean>('sidebar.collapsed')
        .then(collapsed => {
            store.dispatch(SidebarActions.setCollapsedState({ collapse: collapsed }));

            watch<AppStore.All, boolean>(store, s => s.sidebar.collapsed, newValue => {
                localForage.setItem('sidebar.collapsed', newValue);
            });

            return initialState(collapsed, apollo.initialState);
        });

    // TODO catch all errors on init and clear storage
}