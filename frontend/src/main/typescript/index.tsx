require('antd/dist/antd.min.css');
require('../css/styles.css');

import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { ApolloClient, createNetworkInterface } from 'react-apollo';

import { Layout } from './Layout';
import ApolloProvider from "react-apollo/src/ApolloProvider";

const networkInterface = createNetworkInterface({
    uri: 'http://localhost:9000/api/graphql' // TODO configuration via webpack
});

const client = new ApolloClient({
    networkInterface: networkInterface
});

ReactDOM.render(
    <ApolloProvider client={client}>
        <Layout />
    </ApolloProvider>,
    document.getElementById("react-app-holder")
);