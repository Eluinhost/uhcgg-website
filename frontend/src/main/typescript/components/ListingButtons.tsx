import * as React from 'react';

export interface ListingButtonsProps {
    hasMore: boolean,
    loading: boolean,
    fetchMore: () => void
    refetch: () => void
}

export const ListingButtons: React.SFC<ListingButtonsProps> = ({ hasMore, loading, refetch, fetchMore }) =>
    <div className="pt-button-group listing-buttons">
        { hasMore && <button
            className="pt-button pt-icon-ellipsis"
            role="button"
            onClick={fetchMore}
            disabled={loading}
            >
            Show more
        </button> }
        <button
            className="pt-button pt-icon-reload"
            disabled={loading}
            onClick={refetch}
            role="button"
            >
            Refresh
        </button>
    </div>;