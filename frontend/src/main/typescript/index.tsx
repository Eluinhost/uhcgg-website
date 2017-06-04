require('react-hot-loader/patch');

require('antd/dist/antd.min.css');
require('../css/main.css');

import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { ApolloClient, createNetworkInterface } from 'react-apollo';
import { AppContainer } from 'react-hot-loader';

import { App } from './components/App';
import ApolloProvider from "react-apollo/src/ApolloProvider";

const networkInterface = createNetworkInterface({
    uri: 'http://localhost:9000/api/graphql' // TODO configuration via webpack
});

const client = new ApolloClient({
    networkInterface: networkInterface
});

const root = document.createElement('div');
document.body.appendChild(root);

const render = (NextApp: React.SFC<{}>) => ReactDOM.render(
    <AppContainer>
        <ApolloProvider client={client}>
            <NextApp />
        </ApolloProvider>
    </AppContainer>,
    root
);

render(App);

if (module.hot) {
    module.hot.accept('./components/App', () => {
        render(require('./components/App').App);
    })
}