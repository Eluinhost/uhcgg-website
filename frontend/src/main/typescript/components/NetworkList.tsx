import * as React from 'react';
import { graphql, InjectedGraphQLProps } from 'react-apollo';
import { Table } from 'antd';
import { TableColumnConfig } from 'antd/lib/table/Table';
import { Link } from 'react-router-dom';

import queryNetworks = require('../../graphql/queryNetworks.graphql');
import { NetworksQuery } from '../graphql';

interface IServer {
    id: string,
    name: string,
    ip: string,
    port: number | null,
    address: string | null,
    location: string,
    region: {
        short: string
    }
}

interface INetwork {
    id: string,
    name: string,
    tag: string,
    owner: {
        username: string
    },
    servers: Array<IServer>
}

const networkColumns: TableColumnConfig<INetwork>[] = [
    {
        title: 'Tag',
        dataIndex: 'tag',
        width: 150,
        render: text => <span style={{ fontWeight: 'bold' }}>{ text }</span>
    },
    {
        title: 'Name',
        dataIndex: 'name',
        width: 150
    },
    {
        title: 'Server Count',
        dataIndex: 'servers.length',
        width: 100
    },
    {
        title: 'Owner',
        dataIndex: 'owner.username',
        render: text => <Link to="/">{ text }</Link> // TODO point to userpage?
    },
];

const serverColumns: TableColumnConfig<IServer>[] = [
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
        title: 'location',
        dataIndex: 'location'
    }
];

class NetworkTable extends Table<INetwork> {}
class ServerTable extends Table<IServer> {}

export const NetworkListComponent: React.SFC<InjectedGraphQLProps<NetworksQuery>> = ({ data }) => {
    if (!data)
        return <h4>No data found</h4>;

    if (data.error)
        return <h4>{ data.error || 'Unknown Error' }</h4>;

    return <NetworkTable
        loading={data.loading}
        style={{ marginTop: 30 }}
        dataSource={data.networks || []}
        columns={networkColumns}
        rowKey={_ => _.id}
        pagination={false}
        bordered={true}
        expandedRowRender={(record: INetwork) =>
            <ServerTable
                dataSource={record.servers}
                columns={serverColumns}
                rowKey={_ => _.id}
                pagination={false}
                bordered={false}
                size="small"
            />
        }
    />;
};

export const NetworkList = graphql(queryNetworks)(NetworkListComponent);