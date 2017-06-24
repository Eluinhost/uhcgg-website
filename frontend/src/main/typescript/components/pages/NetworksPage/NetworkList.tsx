import * as React from 'react';
import { OptionProps } from 'react-apollo';
import { Table } from 'antd';
import { TableColumnConfig } from 'antd/lib/table/Table';
import { Link } from 'react-router-dom';

import queryNetworks = require('../../../../graphql/NetworkList.graphql');

import { NetworkListNetworkFragment, NetworkListQuery, NetworkListQueryVariables } from '../../../graphql';
import { simpleGraphqlCursor } from '../../../apolloHelpers';
import { ServerList } from './ServerList';
import { ListingButtons } from '../../ListingButtons';

const tableColumns: TableColumnConfig<NetworkListNetworkFragment>[] = [
    {
        title: 'Tag',
        dataIndex: 'tag',
        render: text => <span style={{ fontWeight: 'bold' }}>{ text }</span>
    },
    {
        title: 'Name',
        dataIndex: 'name',
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

class NetworkTable extends Table<NetworkListNetworkFragment> {}

export const NetworkList = simpleGraphqlCursor({
    query: queryNetworks,
    initVariables(): NetworkListQueryVariables {
        return {
            after: null,
            first: 10
        }
    },
    mapVariables(props: OptionProps<{}, NetworkListQuery>): NetworkListQueryVariables {
        return {
            after: props.data!.networks.pageInfo.endCursor,
            first: 10
        }
    },
    mergeResults(last: NetworkListQuery, next: NetworkListQuery): NetworkListQuery {
        return {
            networks: {
                ...last.networks,
                pageInfo: next.networks.pageInfo,
                edges: [
                    ...last.networks.edges!,
                    ...next.networks.edges!
                ]
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

        let networks: Array<NetworkListNetworkFragment> = [];
        let hasMore: boolean = false;

        if (props.data.networks) {
            networks = props.data.networks.edges!.map(edge => edge.node);
            hasMore = props.data.networks.pageInfo.hasNextPage;
        }

        return <div>
            <h2>
                All Networks
            </h2>
            <NetworkTable
                loading={props.data.loading}
                style={{ marginTop: 30 }}
                dataSource={networks}
                columns={tableColumns}
                rowKey={network => network.id}
                pagination={false}
                bordered={true}
                expandedRowRender={(network: NetworkListNetworkFragment) =>
                    <div>
                        <h2>{ network.name } [{ network.tag }]</h2>

                        <div style={{ margin: 20 }}>
                            { network.description }
                        </div>

                        <ServerList networkId={network.id} serverName={network.name} />
                    </div>
                }
                footer={() =>  <ListingButtons
                    loading={props.data!.loading}
                    hasMore={hasMore}
                    refetch={() => props.data!.refetch()}
                    fetchMore={() => props.data!.fetchAnotherPage()}
                />}
            />
        </div>;
    }
});