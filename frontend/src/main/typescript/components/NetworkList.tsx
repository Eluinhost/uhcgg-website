import * as React from 'react';
import { graphql, InjectedGraphQLProps } from 'react-apollo';

import queryNetworks = require('../../graphql/queryNetworks.graphql');
import { NetworksQuery } from '../graphql';

export const NetworkListComponent: React.SFC<InjectedGraphQLProps<NetworksQuery>> = ({ data }) => {
    if (!data)
        return <h4>No data found</h4>;

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
};

export const NetworkList = graphql(queryNetworks)(NetworkListComponent);