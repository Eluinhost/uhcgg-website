import * as React from 'react';
import { graphql, InjectedGraphQLProps } from 'react-apollo';

import queryNetworks = require('../graphql/queryNetworks.graphql');
import { NetworksQuery } from './graphql';

@graphql(queryNetworks)
export class ApolloExample extends React.PureComponent<InjectedGraphQLProps<NetworksQuery>, undefined> {
    render() {
        const { data } = this.props;

        if (!data)
            return null;

        if (data.loading)
            return <h4>Loading...</h4>;

        if (data.error || !data.networks)
            return <h4>{ data.error || 'Unknown Error' }</h4>;

        return <ul>
            { data.networks.map(network => <li key={ network.id }>
                <h4>{ network.name }</h4>
                <ul>
                    { network.servers.map(server => <li key={ server.id }>
                        { `${server.name} - ${server.ip}` }
                    </li>) }
                </ul>
            </li>) }
        </ul>;
    }
}
