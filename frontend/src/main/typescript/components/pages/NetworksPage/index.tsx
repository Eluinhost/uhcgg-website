import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { NetworkList } from './NetworkList';

export const NetworksPage: React.SFC<RouteComponentProps<void>> = () =>
    <div style={{ margin: 20 }}>
        <NetworkList />
    </div>;
