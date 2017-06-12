import { Store as ApolloStore } from 'apollo-client/store'

export namespace AppStore {
    export type Sidebar = {
        collapsed: boolean
    }

    export type All = {
        sidebar: Sidebar,
        apollo: ApolloStore
    }
}
