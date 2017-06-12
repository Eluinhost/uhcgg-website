import * as React from 'react';
import { Link, withRouter, RouteComponentProps } from 'react-router-dom';
import { Menu, Layout, Icon } from 'antd';
import {SidebarActions} from "../actions";
import {compose} from "ramda";
import {connect_SD} from "../reduxUtil";

const logoUrl = require('../../images/logo.png');

export type SidebarStateProps = {
    collapsed: boolean
}

export type SidebarDispatchProps = {
    toggle: () => void
}

export type SidebarProps = RouteComponentProps<void> & SidebarStateProps & SidebarDispatchProps;

const SidebarComponent: React.SFC<SidebarProps> = ({ location: { pathname }, collapsed, toggle }) =>
    <Layout.Sider width={200} style={{ height: '100vh', overflow: 'auto' }} collapsible={true} collapsed={collapsed} onCollapse={toggle} >
        <div className="logo">
            <Link to="/">
                <img src={logoUrl} alt="logo" />
                <div className="nav-text">uhc.gg</div>
            </Link>
        </div>

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
            <Menu.Item key="/networks">
                <Link to="/networks">
                    <span>
                        <Icon type="database" />
                        <span className="nav-text">Networks</span>
                    </span>
                </Link>
            </Menu.Item>
        </Menu>
    </Layout.Sider>;

// required for antd to work properly
(SidebarComponent as any).__ANT_LAYOUT_SIDER = true;

export const Sidebar: React.ComponentClass<{}> =
    withRouter<{}>(
        connect_SD<SidebarStateProps, SidebarDispatchProps>(
            state => ({
                collapsed: state.sidebar.collapsed,
            }),
            dispatch => ({
                toggle: () => dispatch(SidebarActions.toggle())
            })
        )(SidebarComponent)
    );
