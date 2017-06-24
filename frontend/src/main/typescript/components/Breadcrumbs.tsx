import * as React from 'react';
import { Link, Route } from 'react-router-dom';

const RouteBreadcrumb: React.SFC<{ url: string, text: string, icon: string }> =
    ({ url, text, icon }) =>
        <Route path={url} component={() =>
            <li>
                <Link to={url} className="pt-breadcrumb">
                    <span>
                        <span className={`pt-icon pt-icon-${icon}`}/>
                        <span className="nav-text">{text}</span>
                    </span>
                </Link>
            </li>
        } />;

export const Breadcrumbs: React.SFC<{}> = () =>
    <ul className="pt-breadcrumbs">
        <RouteBreadcrumb url="/" icon="home" text="Home" />
        <RouteBreadcrumb url="/register" icon="user" text="Register" />
        <RouteBreadcrumb url="/networks" icon="database" text="Networks" />
        <RouteBreadcrumb url="/dev" icon="git-repo" text="API"/>
        <RouteBreadcrumb url="/dev/schema" icon="layout" text="Schema"/>
    </ul>;