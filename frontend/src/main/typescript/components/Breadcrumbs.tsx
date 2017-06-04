import * as React from 'react';
import { Link, Switch, Route } from 'react-router-dom';
import { Breadcrumb, Icon } from 'antd';

const HomeCrumb: React.SFC<{}> = () =>
    <Breadcrumb.Item>
        <Link to="/">
            <span>
                <Icon type="home" />
                <span className="nav-text">Home</span>
            </span>
        </Link>
    </Breadcrumb.Item>;

const RegisterCrumb: React.SFC<{}> = () =>
    <Breadcrumb.Item>
        <Link to="/register">
            <span>
                <Icon type="user" />
                <span className="nav-text">Register</span>
            </span>
        </Link>
    </Breadcrumb.Item>;

const ExampleCrumb: React.SFC<{}> = () =>
    <Breadcrumb.Item>
        <Link to="/example">
            <span>
                <Icon type="database" />
                <span className="nav-text">Example Page</span>
            </span>
        </Link>
    </Breadcrumb.Item>;

export const Breadcrumbs: React.SFC<{}> = () =>
    <Breadcrumb style={{ margin: '12px 0' }}>
        <Route path="/" component={ HomeCrumb }/>
        <Route path="/register" component={ RegisterCrumb } />
        <Route path="/example" component={ ExampleCrumb } />
    </Breadcrumb>;