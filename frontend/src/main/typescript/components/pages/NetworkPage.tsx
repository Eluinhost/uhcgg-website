import * as React from 'react';
import { graphql } from "react-apollo";
import {NetworkViewQuery, NetworkViewQueryVariables} from "../../graphql";
import {RouteComponentProps} from "react-router";
import {NonIdealState, Spinner} from "@blueprintjs/core";
import {ServerList} from "./NetworksPage/ServerList";
import {Markdown} from "../Markdown";
import {Link} from "react-router-dom";

const networkView = require('../../../graphql/NetworkView.graphql');

export interface NetworkViewRouterParams {
    id: string
}

export const NetworkView = graphql<NetworkViewQuery, RouteComponentProps<NetworkViewRouterParams>>(
    networkView,
    {
        options: (props: RouteComponentProps<NetworkViewRouterParams>) => ({
            variables: {
                id: btoa(`Network:${props.match.params.id}`)
            } as NetworkViewQueryVariables
        })
    }
)(({ data }) => {
    if (!data) throw new Error('expected data');

    if (data.loading) {
        return <NonIdealState action={<Spinner />} title="Loading data..." />
    }

    if (!data.node) {
        return <NonIdealState title="Not found" visual="geosearch"/>
    }

    return <div className="network-view">
        <div className="network-view-header">
            <h1>{ data.node.name } <span className="pt-text-muted">[{data.node.tag}]</span></h1>
            <div>
                <span className={`pt-icon pt-icon-user`} style={{ paddingRight: 5 }}/>
                <Link to={`/users/${data.node.owner.username}/`}>/u/{data.node.owner.username}</Link>
            </div>
        </div>
        <div className="network-view-content">
            <h3>Servers</h3>
            <ServerList networkId={data.node.id}/>

            <h3 className="network-view-rules-header">Rules/Description</h3>
            <Markdown markdown={data.node.description} />
        </div>
     </div>;
});