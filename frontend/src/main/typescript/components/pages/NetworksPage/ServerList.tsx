import * as React from 'react';
import { Table } from 'antd';
import { TableColumnConfig} from 'antd/lib/table/Table';
import { Link } from 'react-router-dom';
import { OptionProps } from 'react-apollo';

import { simpleGraphqlCursor } from '../../../apolloHelpers';
import { ServerListQuery, ServerListQueryVariables, ServerListServerFragment} from '../../../graphql';
import queryServers = require('../../../../graphql/ServerList.graphql');
import { ListingButtons } from '../../ListingButtons';

const tableColumns: TableColumnConfig<ServerListServerFragment>[] = [
    {
        title: 'Name',
        dataIndex: 'name'
    },
    {
        title: 'Address',
        dataIndex: 'ip',
        render: (text, record) =>
            <span>
                { record.address ? record.address : record.ip }:{ record.port ? record.port : '25565'}
            </span>
    },
    {
        title: 'Region',
        dataIndex: 'region.short'
    },
    {
        title: 'Location',
        dataIndex: 'location'
    },
    {
        title: 'Owner',
        dataIndex: 'owner.username',
        render: text => <Link to={`/users/${text}`}>/u/{ text}</Link>
    },
    {
        title: 'Id',
        dataIndex: 'rawId'
    }
];

class ServerTable extends Table<ServerListServerFragment> {}

export interface ServerListProps {
    networkId: string,
    serverName: string,
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

        if (props.data.error) {
            return <div>
                <h4>
                    {props.data.error || 'Unknown Error'}
                </h4>
                <ListingButtons
                    hasMore={false}
                    loading={props.data!.loading}
                    refetch={props.data!.refetch}
                    fetchMore={() => {}}
                />
            </div>;
        }

        let servers: Array<ServerListServerFragment> = [];
        let hasMore: boolean = false;

        if (props.data.node && props.data.node.servers) {
            servers = props.data.node.servers.edges!.map(edge => edge.node);
            hasMore = props.data.node.servers.pageInfo.hasNextPage;
        }

        return <ServerTable
            loading={props.data.loading}
            style={{marginTop: 30}}
            dataSource={servers}
            columns={tableColumns}
            rowKey={_ => _.id}
            pagination={false}
            bordered={false}
            size="small"
            title={() => `Servers belonging to ${props.serverName}`}
            footer={() =>  <ListingButtons
                loading={props.data!.loading}
                hasMore={hasMore}
                refetch={() => props.data!.refetch()}
                fetchMore={() => props.data!.fetchAnotherPage()}
            />}
        />;
    }
});