import * as React from 'react';
import { Link, Route } from 'react-router-dom';
import {RouteComponentProps} from "react-router";

const RouteBreadcrumb: React.SFC<{ url: string, text: string, icon: string }> =
    ({ url, text, icon }) =>
        <Route path={url} component={() =>
            <li>
                <Link to={url} className="pt-breadcrumb">
                    <span className={`pt-icon pt-icon-${icon}`}/>
                    <span className="nav-text">{text}</span>
                </Link>
            </li>
        } />;

interface ByIdRouteParams {
    id: string
}

const ByIdBreadcrumbLink: React.SFC<RouteComponentProps<ByIdRouteParams>> =
    ({ match }) => <li>
        <Link to={`${match.url}`} className="pt-breadcrumb">
            <span className="nav-text">{match.params.id}</span>
        </Link>
    </li>;

const ByIdBreadcrumb: React.SFC<{ url: string }> =
    ({ url }) => <Route path={`${url}/:id`} component={ByIdBreadcrumbLink}/>;


export const Breadcrumbs: React.SFC = () =>
    <ul className="pt-breadcrumbs">
        <RouteBreadcrumb url="/" icon="home" text="Home" />
        <RouteBreadcrumb url="/register" icon="user" text="Register" />
        <RouteBreadcrumb url="/matches" icon="calendar" text="Matches" />
        <RouteBreadcrumb url="/dev" icon="git-repo" text="API" />
        <RouteBreadcrumb url="/dev/schema" icon="layout" text="Schema" />
        <RouteBreadcrumb url="/networks" icon="database" text="Networks" />
        <ByIdBreadcrumb url="/networks" />
    </ul>;