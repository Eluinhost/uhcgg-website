import * as React from 'react';
import { BrowserRouter, Switch, Route, RouteProps } from 'react-router-dom';
import { Breadcrumbs } from './Breadcrumbs';
import { Colors } from '@blueprintjs/core';
import { CSSTransitionGroup } from 'react-transition-group';

import { HomePage } from './pages/HomePage';
import { RegisterPage } from './pages/RegisterPage';
import { NetworksPage } from './pages/NetworksPage';
import { GraphiQLPage } from './pages/GraphiQLPage';
import { GraphQLSchemaPage } from './pages/GraphQLSchemaPage';
import { Sidebar } from './Sidebar';
import { NetworkView } from './pages/NetworkPage';

const PageRoute: React.SFC<RouteProps> = props =>
    <div className="page">
        <Route {...props} />
    </div>;

export const App: React.SFC<{}> = () =>
    <BrowserRouter>
        <div style={{ display: 'flex' }}>
            <Sidebar />
            <div className="app-main-content">
                <Breadcrumbs />
                <Route render={({ location }) => <CSSTransitionGroup
                    component="div"
                    className="app-page-content"
                    transitionName="app-route"
                    transitionEnterTimeout={600}
                    transitionLeaveTimeout={300}
                    transitionEnter={true}
                    transitionLeave={true}
                    >
                    <Switch key={location.key} location={location}>
                        <PageRoute exact path="/" component={HomePage} />
                        <PageRoute path="/register" component={RegisterPage} />
                        <PageRoute path="/networks/:id" component={NetworkView} />
                        <PageRoute path="/networks" component={NetworksPage} />
                        <PageRoute exact path="/dev" component={GraphiQLPage}/>
                        <PageRoute path="/dev/schema" component={GraphQLSchemaPage} />
                    </Switch>
                </CSSTransitionGroup>} />
                <div className="app-footer">
                    <a href="#" style={{ color: Colors.LIGHT_GRAY3 }}>Source Code</a>
                    &nbsp;|&nbsp;
                    <a href="#" style={{ color: Colors.LIGHT_GRAY3 }}>Bugs</a>
                    &nbsp;&nbsp;
                    ©&nbsp;2017&nbsp;Eluinhost
                </div>
            </div>
        </div>
    </BrowserRouter>;