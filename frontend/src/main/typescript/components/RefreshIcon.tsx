import * as React from 'react';
import { QueryProps } from 'react-apollo';
import { Icon } from 'antd';

export const RefreshIcon: React.SFC<QueryProps> = ({ refetch, loading, networkStatus }) => {
    console.log(networkStatus, loading);

    return <Icon type="reload" spin={loading} onClick={() => {
        if (loading)
            return;

        refetch();
    }}/>;
};
