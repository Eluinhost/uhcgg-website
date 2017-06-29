import * as React from 'react';
import { OptionProps } from 'react-apollo';
import { Link } from 'react-router-dom';
import { Collapse, NonIdealState, Spinner } from "@blueprintjs/core";

import { NetworkListNetworkFragment, NetworkListQuery, NetworkListQueryVariables } from '../../../graphql';
import { simpleGraphqlCursor } from '../../../apolloHelpers';
import { ServerList } from './ServerList';
import { ListingButtons } from '../../ListingButtons';

import queryNetworks = require('../../../../graphql/NetworkList.graphql');
import {Markdown} from "../../Markdown";
import {InfinityScroll} from "../../InfinityScroll";

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
            <Link to={`/networks/${this.props.network.rawId}`} onClick={e => e.stopPropagation()}>
                <div className="network-list-row-open pt-icon pt-icon-document-open" />
            </Link>

            <div className="network-list-row-header">
                <div className="network-list-row-title">
                    <span className="network-list-row-name">{ this.props.network.name }</span>
                    <span className="network-list-row-tag">[{ this.props.network.tag }]</span>
                </div>
                <div className="network-list-row-owner">
                    <Link to={`/users/${this.props.network.owner.username}`} onClick={e => e.stopPropagation()}>
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
                    <Markdown className="network-list-row-description" markdown={this.props.network.description} />
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

        if (props.data.loading) {
            return <NonIdealState
                action={<Spinner/>}
                title="Loading data..."
            />;
        }

        if (props.data.error) {
            return <NonIdealState
                action={<ListingButtons
                    hasMore={false}
                    loading={props.data!.loading}
                    refetch={props.data!.refetch}
                    fetchMore={() => {}}
                />}
                title={props.data.error.message || 'Unknown error'}
                visual="error"
            />;
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
                <button
                    className="pt-button pt-icon-reload pt-intent-success refresh-button network-list-refresh"
                    disabled={props.data.loading}
                    onClick={() => props.data!.refetch()}
                    role="button"
                >
                    <span className="pt-icon pt-icon-refresh" />
                    Refresh List
                </button>

                <InfinityScroll
                    trigger={props.data.fetchAnotherPage}
                    hasMore={hasMore}
                    noMoreItemsComponent={<div className="pt-callout pt-intent-danger network-list-footer-callout">
                        <h5>There are no more networks left to load</h5>
                        You can click here to try and re-check:
                        &nbsp;
                        <button
                            className="pt-button pt-icon-reload pt-intent-success"
                            disabled={props.data.loading}
                            onClick={() => props.data!.fetchAnotherPage()}
                            role="button"
                        >
                            <span className="pt-icon pt-icon-refresh" />
                            Load more
                        </button>
                    </div>}
                    loadingComponent={<div className="pt-callout pt-intent-primary pt-icon-search network-list-footer-callout">
                        <h5>Loading more networks...</h5>
                    </div>}
                >
                    { networks.map((network, index) =>
                        <NetworkListRow network={network} key={network.id} index={index} />
                    )}
                </InfinityScroll>
            </div>
        </div>;
    }
});