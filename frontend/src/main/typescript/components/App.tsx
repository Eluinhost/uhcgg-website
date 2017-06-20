import * as React from 'react';
import { BrowserRouter, Switch, Route } from 'react-router-dom';
import { Breadcrumbs } from './Breadcrumbs';
import { Sidebar } from './Sidebar';
import { Layout } from 'antd';
import { CSSProperties } from 'react';

import { HomePage } from './pages/HomePage';
import { RegisterPage } from './pages/RegisterPage';
import { NetworksPage } from './pages/NetworksPage';
import { GraphiQLPage } from './pages/GraphiQLPage';
import { GraphQLSchemaPage } from './pages/GraphQLSchemaPage';

const pageContentStyle: CSSProperties = {
    flex: '1 1 auto',
    overflowY: 'scroll',
    height: 0 // Required otherwise it doesn't have a height set, flexbox handles the height after load though
};

const mainContentStyle: CSSProperties = {
    display: 'flex',
    flexDirection: 'column',
    width: '100%',
    height: '100vh'
};

const footerStyle: CSSProperties = {
    textAlign: 'right',
    paddingTop: 5,
    paddingBottom: 5,
    paddingRight: 15,
    background: '#404040',
    color: 'hsla(0,0%,100%,.67)'
};

export const App: React.SFC<{}> = () =>
    <BrowserRouter>
        <Layout style={{ minHeight: '100%' }} >
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
                    <a href="#">Source Code</a>
                    &nbsp;|&nbsp;
                    <a href="#">Bugs</a>
                    &nbsp;&nbsp;
                    ©&nbsp;2017&nbsp;Eluinhost
                </div>
            </div>
        </Layout>
    </BrowserRouter>;