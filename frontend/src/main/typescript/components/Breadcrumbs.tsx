import * as React from 'react';
import { Link, Route } from 'react-router-dom';
import { Breadcrumb, Icon } from 'antd';
import { CSSProperties } from 'react';

export interface BreadcrumbRoute extends React.SFC<{ url: string, text: string, icon: string}> {
    __ANT_BREADCRUMB_ITEM?: boolean
}

const breadcrumbsStyle: CSSProperties = {
    marginTop: '12px',
    marginLeft: '20px',
    marginBottom: '20px'
};

const RouteBreadcrumb: BreadcrumbRoute = ({ url, text, icon }) =>
    <Route path={url} component={() => <Breadcrumb.Item>
        <Link to={url}>
                <span>
                    <Icon type={icon}/>
                    <span className="nav-text">{text}</span>
                </span>
        </Link>
    </Breadcrumb.Item>} />;
RouteBreadcrumb.__ANT_BREADCRUMB_ITEM = true;

export const Breadcrumbs: React.SFC<{}> = () =>
    <Breadcrumb style={breadcrumbsStyle}>
        <RouteBreadcrumb url="/" icon="home" text="Home" />
        <RouteBreadcrumb url="/register" icon="user" text="Register" />
        <RouteBreadcrumb url="/networks" icon="database" text="Networks" />
        <RouteBreadcrumb url="/dev" icon="api" text="API"/>
        <RouteBreadcrumb url="/dev/schema" icon="share-alt" text="Schema"/>
    </Breadcrumb>;