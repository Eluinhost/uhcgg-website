import { FetchMoreOptions, FetchMoreQueryOptions } from 'apollo-client';
import { DocumentNode } from 'graphql'
import { DefaultChildProps, OptionProps, QueryOpts, graphql } from 'react-apollo';
import { CompositeComponent } from 'react-apollo/lib/graphql';
import * as React from 'react';

export interface FetchAnotherPageProps {
    fetchAnotherPage(): any
}

export interface SimpleGraphqlCursorOptions<TProps, TResult, TVariables> {
    query: DocumentNode,
    initVariables(props: TProps): TVariables,
    mapVariables(props: OptionProps<TProps, TResult>): TVariables,
    mergeResults(last: TResult, next: TResult): TResult,
    component: CompositeComponent<DefaultChildProps<TProps, TResult & FetchAnotherPageProps>>
}

export interface CursorVariables {
    after: string | null;
}

export function simpleGraphqlCursor<TProps, TResult, TVariables extends CursorVariables>(options: SimpleGraphqlCursorOptions<TProps, TResult, TVariables>): React.ComponentClass<TProps> {
    function optionsMapping(props: TProps): QueryOpts {
        return {
            variables: options.initVariables(props)
        }
    }

    function propsMapping(props: OptionProps<TProps, TResult>): any {
        const fetchAnother = () => props.data!.fetchMore({
            query: options.query,
            variables: options.mapVariables(props),
            updateQuery(prev, {fetchMoreResult}): TResult {
                const last = prev as TResult;
                const next = fetchMoreResult as TResult;

                return options.mergeResults(last, next)
            }
        } as FetchMoreQueryOptions & FetchMoreOptions);

        return {
            ...props.ownProps as any,
            mutate: props.mutate,
            data: {
                ...props.data as any,
                fetchAnotherPage: fetchAnother
            } as FetchAnotherPageProps & TResult
        }
    }

    return graphql<TResult & FetchAnotherPageProps, TProps>(options.query, {
        options: optionsMapping,
        props: propsMapping
    })(options.component)
}
