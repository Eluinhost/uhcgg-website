import * as React from 'react';
import { RouteComponentProps } from 'react-router';

export const RegisterErrorPage: React.SFC<RouteComponentProps<void>> = ({ location: { hash }}) =>
    <h1>
        Error:
        { hash ? decodeURIComponent(hash.substring(1)) : 'Unknown error' }
    </h1>;