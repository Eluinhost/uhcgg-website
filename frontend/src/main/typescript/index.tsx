import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { ApolloClient, createNetworkInterface } from 'react-apollo';

import { ApolloExample } from './ApolloExample';
import ApolloProvider from "react-apollo/src/ApolloProvider";

const networkInterface = createNetworkInterface({
    uri: 'http://localhost:9000/api/graphql' // TODO configuration via webpack
});

const client = new ApolloClient({
    networkInterface: networkInterface
});

ReactDOM.render(
    <ApolloProvider client={client}>
        <ApolloExample />
    </ApolloProvider>,
    document.getElementById("react-app-holder")
);