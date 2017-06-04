import * as React from 'react';
import { Link, withRouter, RouteComponentProps } from 'react-router-dom';
import { Menu, Layout, Icon } from 'antd';

export interface SiderSFC extends React.SFC<RouteComponentProps<void>> {
    __ANT_LAYOUT_SIDER?: boolean
}

const SidebarComponent: SiderSFC = ({ location: { pathname }}) =>
    <Layout.Sider width={200} style={{ height: '100vh', overflow: 'auto' }} collapsible={true} >
        <div className="logo" />

        <Menu
            mode="inline"
            theme="dark"
            selectedKeys={[ pathname ]}
            style={{ height: '100%' }}
        >
            <Menu.Item key="/">
                <Link to="/">
                    <span>
                        <Icon type="home" />
                        <span className="nav-text">Home</span>
                    </span>
                </Link>
            </Menu.Item>
            <Menu.Item key="/register">
                <Link to="/register">
                    <span>
                        <Icon type="user" />
                        <span className="nav-text">Register</span>
                     </span>
                </Link>
            </Menu.Item>
            <Menu.Item key="/example">
                <Link to="/example">
                    <span>
                        <Icon type="database" />
                        <span className="nav-text">Example Page</span>
                    </span>
                </Link>
            </Menu.Item>
        </Menu>
    </Layout.Sider>;

SidebarComponent.__ANT_LAYOUT_SIDER = true;

export const Sidebar: React.ComponentClass<{}> = withRouter(SidebarComponent);