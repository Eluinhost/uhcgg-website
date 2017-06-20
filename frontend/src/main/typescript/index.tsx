import {sidebar} from "./reducers";

require('react-hot-loader/patch');

require('antd/dist/antd.min.css');
require('nprogress/nprogress.css');
require('graphiql/graphiql.css');
require('../css/main.css');
require('graphql-voyager/dist/voyager.css');

import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { ApolloClient, ApolloProvider, createNetworkInterface } from 'react-apollo';
import { HTTPNetworkInterface } from 'apollo-client/transport/networkInterface';
import {applyMiddleware, combineReducers, compose, createStore, Store} from 'redux';
import { AppContainer } from 'react-hot-loader';
import { omit } from 'ramda'

import { App } from './components/App';
import { LoaderBarMiddleware } from './LoaderBarMiddleware';
import {AppStore} from './AppStore';
import {generateInitialState} from "./storage";

const apolloClient: ApolloClient = (() => {
    const networkInterface: HTTPNetworkInterface = createNetworkInterface({
        uri: 'http://localhost:9000/api/graphql' // TODO configuration via webpack
    });

    const loaderMiddleware: LoaderBarMiddleware = new LoaderBarMiddleware();

    networkInterface.use([loaderMiddleware]);
    networkInterface.useAfter([loaderMiddleware]);

    return new ApolloClient({
        networkInterface: networkInterface
    });
})();

// use extension if available
const composeEnhancers: any =
    window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
        ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({
            stateSanitizer: (state: any) => ({
                ...state,
                apollo: omit(['data'], state.apollo)
            })
        })
        : compose;

const reduxStore: Store<AppStore.All> = createStore<AppStore.All>(
    combineReducers<AppStore.All>({
        sidebar,
        apollo: apolloClient.reducer()
    }),
    composeEnhancers(
        applyMiddleware(apolloClient.middleware())
    )
);

// Add root element for React to body for rendering to
const root = document.createElement('div');
document.body.appendChild(root);

const render = (NextApp: React.SFC<{}>) => ReactDOM.render(
    <AppContainer>
        <ApolloProvider client={apolloClient} store={reduxStore} >
            <NextApp />
        </ApolloProvider>
    </AppContainer>,
    root
);

generateInitialState(reduxStore, apolloClient)
    .then(() => {
        // Initial render to page
        render(App);

        // If we're hot reloading listen for changes on root App object and fire re-renders as required
        if (module.hot) {
            module.hot.accept('./components/App', () => {
                render(require('./components/App').App);
            })
        }
    })
    .catch(err => console.error(err));