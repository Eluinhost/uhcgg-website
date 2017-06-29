import * as React from "react";
import {MatchListMatchFragment, MatchListQuery, MatchListQueryVariables} from "../../../graphql";
import {OptionProps} from "react-apollo";
import {Collapse, NonIdealState, Spinner} from "@blueprintjs/core";
import {ListingButtons} from "../../ListingButtons";
import {simpleGraphqlCursor} from "../../../apolloHelpers";
import {InfinityScroll} from "../../InfinityScroll";
import {Link} from "react-router-dom";

const queryMatches = require('../../../../graphql/MatchList.graphql');

export interface MatchListRowProps {
    match: MatchListMatchFragment,
    index: number
}

export class MatchListRow extends React.Component<MatchListRowProps, { isOpen: boolean }> {
    state = {
        isOpen: false
    };

    toggle = () => this.setState(prev => ({
        isOpen: !prev.isOpen
    }));

    render() {
        return <div className="pt-card pt-elevation-0 pt-interactive match-list-row" onClick={this.toggle}>
            <div className="match-list-row-index pt-text-muted">
                { this.props.index + 1 }
            </div>
            <Link to={`/matches/${this.props.match.rawId}`} onClick={e => e.stopPropagation()}>
                <div className="match-list-row-open pt-icon pt-icon-document-open" />
            </Link>

            <div className="match-list-row-header">
                <div className="match-list-row-title">
                    <span className="match-list-row-style">{ this.props.match.style.shortName }</span>
                </div>
                <div className="network-list-row-owner">
                    <Link to={`/users/${this.props.match.host.username}`} onClick={e => e.stopPropagation()}>
                        /u/{ this.props.match.host.username}
                    </Link>
                </div>
            </div>

            <div onClick={e => e.stopPropagation()}>
                <Collapse isOpen={this.state.isOpen} className="match-list-row-contents">
                    <h5>Something in here</h5>
                    { this.state.isOpen && <span>yeah</span> }
                </Collapse>
            </div>
        </div>;
    }
}

export const MatchList = simpleGraphqlCursor({
    query: queryMatches,
    initVariables(): MatchListQueryVariables {
        return {
            after: null,
            first: 20
        }
    },
    mapVariables(props: OptionProps<{}, MatchListQuery>): MatchListQueryVariables {
        return {
            after: props.data!.upcomingMatches.pageInfo.endCursor,
            first: 20
        }
    },
    mergeResults(last: MatchListQuery, next: MatchListQuery): MatchListQuery {
        return {
            upcomingMatches: {
                ...last.upcomingMatches,
                pageInfo: next.upcomingMatches.pageInfo,
                edges: [
                    ...last.upcomingMatches.edges!,
                    ...next.upcomingMatches.edges!
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

        let matches: Array<MatchListMatchFragment> = [];
        let hasMore: boolean = false;

        if (props.data.upcomingMatches) {
            matches = props.data.upcomingMatches.edges!.map(edge => edge.node);
            hasMore = props.data.upcomingMatches.pageInfo.hasNextPage;
        }

        return <div>
            <h2>
                Upcoming Matches
            </h2>

            <div className="upcoming-matches-list">
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
                        <h5>There are no more matches left to load</h5>
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
                        <h5>Loading more matches...</h5>
                    </div>}
                >
                    { matches.map((match, index) =>
                        <MatchListRow match={match} key={match.id} index={index} />
                    )}
                </InfinityScroll>
            </div>
        </div>;
    }
});