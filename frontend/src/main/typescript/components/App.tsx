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

const pageContentStyle: CSSProperties = {
    flex: '1 1 auto',
    overflowY: 'scroll',
    height: 0 // Required otherwise it doesn't have a height set, flexbox handles the height after load though
};

const mainContentStyle: CSSProperties = {
    display: 'flex',
    flexDirection: 'column',
    width: '100%',
    height: '100vh',
    overflowX: 'auto'
};

const footerStyle: CSSProperties = {
    textAlign: 'right',
    paddingTop: 5,
    paddingBottom: 5,
    paddingRight: 15,
    background: Colors.DARK_GRAY2,
    color: Colors.LIGHT_GRAY1
}; // TODO css this stuff

export const App: React.SFC<{}> = () =>
    <BrowserRouter>
        <div style={{ display: 'flex' }}>
            <Sidebar />
            <div style={mainContentStyle}>
                <Breadcrumbs />
                <div style={pageContentStyle}>
                    <Switch>
                        <Route exact path="/" component={HomePage} />
                        <Route path="/register" component={RegisterPage} />
                        <Route path="/networks" component={NetworksPage} />
                        <Route exact path="/dev" component={GraphiQLPage}/>
                        <Route path="/dev/schema" component={GraphQLSchemaPage} />
                    </Switch>
                </div>
                <div style={footerStyle}>
                    <a href="#" style={{ color: Colors.LIGHT_GRAY3 }}>Source Code</a>
                    &nbsp;|&nbsp;
                    <a href="#" style={{ color: Colors.LIGHT_GRAY3 }}>Bugs</a>
                    &nbsp;&nbsp;
                    ©&nbsp;2017&nbsp;Eluinhost
                </div>
            </div>
        </div>
    </BrowserRouter>;