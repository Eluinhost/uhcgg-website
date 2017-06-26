import * as React from 'react';
import { Link } from 'react-router-dom';
import { OptionProps } from 'react-apollo';

import { simpleGraphqlCursor } from '../../../apolloHelpers';
import { ServerListQuery, ServerListQueryVariables, ServerListServerFragment} from '../../../graphql';
import queryServers = require('../../../../graphql/ServerList.graphql');
import { ListingButtons } from '../../ListingButtons';
import {Position, Tooltip, NonIdealState, Spinner} from "@blueprintjs/core";

export const ServerListRow: React.SFC<{ server: ServerListServerFragment }> = ({ server }) =>
    <div className="server-list-row">
        <span className="server-list-row-name">{server.name}</span>

        <span className="pt-tag pt-intent-primary">{server.region.short}</span>
        <span className="pt-tag pt-intent-success">{server.location}</span>

        <span className="server-list-row-address">
            {
                server.address
                    ? <Tooltip content={server.ip} position={Position.TOP}>
                        { server.address }
                    </Tooltip>
                    : <span>{ server.ip }</span>
            }
            {
                server.port && server.port != 25565 && <span>:{server.port}</span>
            }
        </span>

        <span className="server-list-row-owner">
            <Link to={`/users/${server.owner.username}`}>
                /u/{ server.owner.username }
            </Link>
        </span>
    </div>;

export interface ServerListProps {
    networkId: string
}

export const ServerList = simpleGraphqlCursor({
    query: queryServers,
    initVariables(props: ServerListProps): ServerListQueryVariables {
        return {
            networkId: props.networkId,
            after: null,
            first: 5
        }
    },
    mapVariables(props: OptionProps<ServerListProps, ServerListQuery>): ServerListQueryVariables {
        return {
            networkId: props.ownProps.networkId,
            after: props.data!.node!.servers.pageInfo.endCursor,
            first: 5
        }
    },
    mergeResults(last: ServerListQuery, next: ServerListQuery): ServerListQuery {
        return {
            ...last,
            node: {
                ...last.node,
                servers: {
                    ...last.node!.servers,
                    edges: [ // combine edges
                        ...last.node!.servers.edges!,
                        ...next.node!.servers.edges!
                    ],
                    pageInfo: next.node!.servers.pageInfo // replace pageInfo
                }
            }
        }
    },
    component: props => {
        if (!props.data) {
            throw new Error('expected data');
        }

        if (props.data.loading) {
            return <NonIdealState
                action={<Spinner/>}
                title="Loading data..."
            />;
        }

        if (props.data.error) {
            return <NonIdealState
                action={<ListingButtons
                    hasMore={false}
                    loading={props.data!.loading}
                    refetch={props.data!.refetch}
                    fetchMore={() => {}}
                />}
                title={props.data.error.message || 'Unknown error'}
                visual="error"
            />;
        }

        let servers: Array<ServerListServerFragment> = [];
        let hasMore: boolean = false;

        if (props.data.node && props.data.node.servers) {
            servers = props.data.node.servers.edges!.map(edge => edge.node);
            hasMore = props.data.node.servers.pageInfo.hasNextPage;
        }

        return <div className="server-list">
            { servers.map(server => <ServerListRow server={server} key={server.id} />) }

            <ListingButtons
                loading={props.data!.loading}
                hasMore={hasMore}
                refetch={() => props.data!.refetch()}
                fetchMore={() => props.data!.fetchAnotherPage()}
            />
        </div>;
    }
});