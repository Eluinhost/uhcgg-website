import * as React from 'react';
import { Button } from 'antd';

export interface ListingButtonsProps {
    hasMore: boolean,
    loading: boolean,
    fetchMore: () => void
    refetch: () => void
}

export const ListingButtons: React.SFC<ListingButtonsProps> = ({ hasMore, loading, refetch, fetchMore }) =>
    <div style={{ textAlign: 'center' }}>
        <Button.Group>
            { hasMore && <Button
                loading={loading}
                onClick={fetchMore}
                icon="ellipsis"
            >
                Show more
            </Button> }
            <Button
                loading={loading}
                icon="reload"
                onClick={refetch}
            >
                Refresh
            </Button>
        </Button.Group>
    </div>;