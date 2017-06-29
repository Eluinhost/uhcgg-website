import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { MatchList } from './MatchList';

export const MatchesPage: React.SFC<RouteComponentProps<void>> = () =>
    <div style={{ margin: 20 }}>
        <MatchList />
    </div>;
