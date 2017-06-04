import * as React from 'react';
import { Link } from 'react-router-dom';
import { Menu, Layout, Icon } from 'antd';

export interface SiderSFC extends React.SFC<{}> {
    __ANT_LAYOUT_SIDER?: boolean
}

export const Sidebar: SiderSFC = () =>
    <Layout.Sider width={200} style={{ height: '100vh', overflow: 'auto' }} collapsible={true} >
        <div className="logo" />

        <Menu
            mode="inline"
            theme="dark"
            defaultSelectedKeys={['1']}
            defaultOpenKeys={['sub1']}
            style={{ height: '100%' }}
        >
            <Menu.Item key="1">
                <Link to="/">
                    <span>
                        <Icon type="home" />
                        <span className="nav-text">Home</span>
                      </span>
                </Link>
            </Menu.Item>
            <Menu.Item key="2">
                <Link to="/register">
                    <span>
                        <Icon type="user" />
                        <span className="nav-text">Register</span>
                      </span>
                </Link>
            </Menu.Item>
            <Menu.Item key="3">
                <Link to="/example">
                    <span>
                        <Icon type="database" />
                        <span className="nav-text">Home</span>
                    </span>
                </Link>
            </Menu.Item>
        </Menu>
    </Layout.Sider>;

Sidebar.__ANT_LAYOUT_SIDER = true;