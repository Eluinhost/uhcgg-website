import { Store as ApolloStore } from 'apollo-client/store'
import { FormStateMap } from 'redux-form';

export namespace AppStore {
    export type Sidebar = {
        collapsed: boolean
    }

    export type All = {
        sidebar: Sidebar,
        apollo: ApolloStore,
        form: FormStateMap
    }
}
