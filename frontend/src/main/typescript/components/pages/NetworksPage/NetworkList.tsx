import * as React from 'react';
import { OptionProps } from 'react-apollo';
import { Link } from 'react-router-dom';
import { Collapse } from "@blueprintjs/core";
import * as Snuownd from 'snuownd';

import { NetworkListNetworkFragment, NetworkListQuery, NetworkListQueryVariables } from '../../../graphql';
import { simpleGraphqlCursor } from '../../../apolloHelpers';
import { ServerList } from './ServerList';
import { ListingButtons } from '../../ListingButtons';

import queryNetworks = require('../../../../graphql/NetworkList.graphql');

const parser = Snuownd.getParser();

export interface NetworkListRowProps {
    network: NetworkListNetworkFragment,
    index: number
}

export class NetworkListRow extends React.Component<NetworkListRowProps, { isOpen: boolean }> {
    constructor(props: NetworkListRowProps) {
        super(props);

        this.state = {
            isOpen: false
        }
    }

    toggle = () => {
        this.setState(prev => ({
            isOpen: !prev.isOpen
        }))
    };

    render() {
        return <div className="pt-card pt-elevation-0 pt-interactive network-list-row" onClick={this.toggle}>
            <div className="network-list-row-index pt-text-muted">
                { this.props.index + 1 }
            </div>

            <div className="network-list-row-header">
                <div className="network-list-row-title">
                    <span className="network-list-row-name">{ this.props.network.name }</span>
                    <span className="network-list-row-tag">[{ this.props.network.tag }]</span>
                </div>
                <div className="network-list-row-owner">
                    <Link to={`/users/${this.props.network.owner.username}`}>
                        /u/{ this.props.network.owner.username}
                    </Link>
                </div>
            </div>

            <div onClick={e => e.stopPropagation()}>
                <Collapse isOpen={this.state.isOpen} className="network-list-row-contents">
                    <div className="network-list-row-servers">
                        <h5>Servers</h5>

                        { this.state.isOpen && <ServerList networkId={this.props.network.id} /> }
                    </div>

                    <h5>Network Description</h5>
                    <pre className="network-list-row-description" dangerouslySetInnerHTML={{ __html: parser.render(this.props.network.description)}} />
                </Collapse>
            </div>
        </div>;
    }
}

export const NetworkList = simpleGraphqlCursor({
    query: queryNetworks,
    initVariables(): NetworkListQueryVariables {
        return {
            after: null,
            first: 10
        }
    },
    mapVariables(props: OptionProps<{}, NetworkListQuery>): NetworkListQueryVariables {
        return {
            after: props.data!.networks.pageInfo.endCursor,
            first: 10
        }
    },
    mergeResults(last: NetworkListQuery, next: NetworkListQuery): NetworkListQuery {
        return {
            networks: {
                ...last.networks,
                pageInfo: next.networks.pageInfo,
                edges: [
                    ...last.networks.edges!,
                    ...next.networks.edges!
                ]
            }
        }
    },
    component: props => {
        if (!props.data) {
            throw new Error('expected data');
        }

        if (props.data.error) {
            return <div>
                <h4>
                    {props.data.error || 'Unknown Error'}
                </h4>
                <ListingButtons
                    hasMore={false}
                    loading={props.data!.loading}
                    refetch={props.data!.refetch}
                    fetchMore={() => {}}
                />
            </div>;
        }

        let networks: Array<NetworkListNetworkFragment> = [];
        let hasMore: boolean = false;

        if (props.data.networks) {
            networks = props.data.networks.edges!.map(edge => edge.node);
            hasMore = props.data.networks.pageInfo.hasNextPage;
        }

        return <div>
            <h2>
                All Networks
            </h2>

            <div className="network-list">
                { networks.map((network, index) => <NetworkListRow network={network} key={network.id} index={index} />) }

                <ListingButtons
                    loading={props.data!.loading}
                    hasMore={hasMore}
                    refetch={() => props.data!.refetch()}
                    fetchMore={() => props.data!.fetchAnotherPage()}
                />
            </div>
        </div>;
    }
});