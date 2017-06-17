import * as React from 'react';
import { graphql, QueryProps } from 'react-apollo';
import { Table, Icon } from 'antd';
import { TableColumnConfig } from 'antd/lib/table/Table';
import { Link } from 'react-router-dom';

import queryNetworks = require('../../graphql/NetworkList.graphql');
import {NetworkListNetworkFragment, NetworkListQuery, NetworkListServerFragment} from '../graphql';

const networkColumns: TableColumnConfig<NetworkListNetworkFragment>[] = [
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
        title: 'Server Count',
        dataIndex: 'servers.length',
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

const serverColumns: TableColumnConfig<NetworkListServerFragment>[] = [
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

class NetworkTable extends Table<NetworkListNetworkFragment> {}
class ServerTable extends Table<NetworkListServerFragment> {}

const RefreshIcon: React.SFC<QueryProps> = ({ refetch, loading, networkStatus }) => {
    console.log(networkStatus, loading);

    return <Icon type="reload" spin={loading} onClick={() => {
        if (loading)
            return;

        refetch();
    }}/>;
};

export const NetworkListComponent = graphql<NetworkListQuery>(queryNetworks)(({ data }) => {
    if (!data)
        return <h4>No data found</h4>;

    if (data.error)
        return <h4>
            { data.error || 'Unknown Error' } <RefreshIcon {...data as QueryProps} />
        </h4>;

    return <div>
        <h2>
            All Networks <RefreshIcon {...data as QueryProps} />
        </h2>
        <NetworkTable
            loading={data.loading}
            style={{ marginTop: 30 }}
            dataSource={data.networks || []}
            columns={networkColumns}
            rowKey={_ => _.id}
            pagination={false}
            bordered={true}
            expandedRowRender={(record: NetworkListNetworkFragment) =>
                <div>
                    <h2>Servers</h2>
                    <ServerTable
                        dataSource={record.servers.edges!.map(_ => _.node)}
                        columns={serverColumns}
                        rowKey={_ => _.id}
                        pagination={false}
                        bordered={false}
                        size="small"
                    />
                    <h2>Description</h2>
                    { record.description }
                </div>
            }
        />
    </div>;
});

export const NetworkList = (NetworkListComponent);