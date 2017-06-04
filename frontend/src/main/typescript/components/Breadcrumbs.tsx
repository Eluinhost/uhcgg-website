import * as React from 'react';
import { Link } from 'react-router-dom';
import { Breadcrumb } from 'antd';

export const Breadcrumbs: React.SFC<{}> = () =>
    <Breadcrumb style={{ margin: '12px 0' }}>
        <Breadcrumb.Item>
            <Link to="/">Home</Link>
        </Breadcrumb.Item>
    </Breadcrumb>;