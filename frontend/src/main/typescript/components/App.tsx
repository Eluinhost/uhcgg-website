import * as React from 'react';
import { BrowserRouter, Switch, Route } from 'react-router-dom';
import { Breadcrumbs } from './Breadcrumbs';
import { Sidebar } from './Sidebar';
import { Layout } from 'antd';

import { HomePage } from './pages/HomePage';
import { RegisterPage } from './pages/RegisterPage';
import { NetworksPage } from './pages/NetworksPage';

export const App: React.SFC<{}> = () =>
    <BrowserRouter>
        <Layout style={{ minHeight: '100%' }} >
            <Sidebar />

            <Layout.Content style={{ overflow: 'auto', height: '100vh' }}>
                <Layout.Content style={{ margin: '0 16px' }}>
                    <Breadcrumbs />

                    <div style={{ textAlign: 'center', padding: 24, minHeight: '100%' }}>
                        <Switch>
                            <Route exact path="/" component={HomePage} />
                            <Route path="/register" component={RegisterPage} />
                            <Route path="/networks" component={NetworksPage} />
                        </Switch>
                    </div>
                </Layout.Content>
                <Layout.Footer style={{ textAlign: 'center' }}>
                    uhc.gg ©2016 Created by Eluinhost
                </Layout.Footer>
            </Layout.Content>
        </Layout>
    </BrowserRouter>;