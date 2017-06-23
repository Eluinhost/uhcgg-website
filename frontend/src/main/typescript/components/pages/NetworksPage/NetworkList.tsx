import * as React from 'react';
import { OptionProps } from 'react-apollo';
import { Table, Button } from 'antd';
import { TableColumnConfig } from 'antd/lib/table/Table';
import { Link } from 'react-router-dom';

import queryNetworks = require('../../../../graphql/NetworkList.graphql');

import { NetworkListNetworkFragment, NetworkListQuery, NetworkListQueryVariables } from '../../../graphql';
import { simpleGraphqlCursor } from '../../../apolloHelpers';
import { ServerList } from './ServerList';
import { RefreshIcon } from '../../RefreshIcon';

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
            return <h4>
                {props.data.error || 'Unknown Error'} <RefreshIcon {...props.data} />
            </h4>;
        }

        let networks: Array<NetworkListNetworkFragment> = [];
        let hasMore: boolean = false;

        if (props.data.networks) {
            networks = props.data.networks.edges!.map(edge => edge.node);
            hasMore = props.data.networks.pageInfo.hasNextPage;
        }

        return <div>
            <h2>
                All Networks <RefreshIcon { ...props.data } />
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
                        <ServerList networkId={network.id} />
                        <h2>Description</h2>
                        { network.description }
                    </div>
                }
            />
            { hasMore && <Button
                type="primary"
                loading={props.data.loading}
                onClick={props.data.fetchAnotherPage}
                icon="ellipsis"
            >
                Show more
            </Button> }
        </div>;
    }
});