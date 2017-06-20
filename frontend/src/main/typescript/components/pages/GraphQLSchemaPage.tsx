import * as React from 'react';
import * as fetch from 'isomorphic-fetch';
import { Voyager } from 'graphql-voyager';

const fetchData: (query: string) => Promise<any> = query => fetch(
    window.location.origin + '/api/graphql', {
        method: 'post',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify({query})
    }
).then(_ => _.json());

export const GraphQLSchemaPage: React.SFC<{}> = () =>
    <Voyager introspection={fetchData} displayOptions={{ skipRelay: true }} />;