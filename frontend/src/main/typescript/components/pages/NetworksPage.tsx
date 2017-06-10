import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { NetworkList } from '../NetworkList';

export const NetworksPage: React.SFC<RouteComponentProps<void>> = () => <div>
    <h1>All networks</h1>

    <NetworkList />
</div>;