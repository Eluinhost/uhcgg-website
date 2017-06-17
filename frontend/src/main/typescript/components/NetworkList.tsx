import * as React from 'react';
import { graphql, QueryProps } from 'react-apollo';
import { Table, Icon } from 'antd';
import { TableColumnConfig } from 'antd/lib/table/Table';
import { Link } from 'react-router-dom';

import queryNetworks = require('../../graphql/queryNetworks.graphql');
import { NetworksQuery } from '../graphql';

interface IServer {
    id: string,
    rawId: string,
    name: string,
    ip: string,
    port: number | null,
    address: string | null,
    location: string,
    region: {
        short: string
    }
    owner: {
        id: string,
        rawId: string,
        username: string
    }
}

interface INetwork {
    id: string,
    rawId: string,
    description: string
    name: string,
    tag: string,
    owner: {
        id: string,
        rawId: string,
        username: string
    },
    servers: Array<IServer>
}

const networkColumns: TableColumnConfig<INetwork>[] = [
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

class NetworkTable extends Table<INetwork> {}
class ServerTable extends Table<IServer> {}

const RefreshIcon: React.SFC<QueryProps> = ({ refetch, loading, networkStatus }) => {
    console.log(networkStatus, loading);

    return <Icon type="reload" spin={loading} onClick={() => {
        if (loading)
            return;

        refetch();
    }}/>;
};

export const NetworkListComponent = graphql<NetworksQuery>(queryNetworks)(({ data }) => {
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
            expandedRowRender={(record: INetwork) =>
                <div>
                    <h2>Servers</h2>
                    <ServerTable
                        dataSource={record.servers}
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