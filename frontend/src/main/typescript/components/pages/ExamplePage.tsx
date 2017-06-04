import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { ApolloExample } from '../ApolloExample';

export const ExamplePage: React.SFC<RouteComponentProps<void>> = () => <ApolloExample />;