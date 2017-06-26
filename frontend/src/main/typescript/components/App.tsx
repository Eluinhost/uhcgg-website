import * as React from 'react';
import { BrowserRouter, Switch, Route } from 'react-router-dom';
import { Breadcrumbs } from './Breadcrumbs';
import { Colors } from '@blueprintjs/core';
import { CSSProperties } from 'react';

import { HomePage } from './pages/HomePage';
import { RegisterPage } from './pages/RegisterPage';
import { NetworksPage } from './pages/NetworksPage';
import { GraphiQLPage } from './pages/GraphiQLPage';
import { GraphQLSchemaPage } from './pages/GraphQLSchemaPage';
import { Sidebar } from './Sidebar';
import { NetworkView } from './pages/NetworkPage';

export const App: React.SFC<{}> = () =>
    <BrowserRouter>
        <div style={{ display: 'flex' }}>
            <Sidebar />
            <div className="app-main-content">
                <Breadcrumbs />
                <div className="app-page-content">
                    <Switch>
                        <Route exact path="/" component={HomePage} />
                        <Route path="/register" component={RegisterPage} />
                        <Route path="/networks/:id" component={NetworkView} />
                        <Route path="/networks" component={NetworksPage} />
                        <Route exact path="/dev" component={GraphiQLPage}/>
                        <Route path="/dev/schema" component={GraphQLSchemaPage} />
                    </Switch>
                </div>
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